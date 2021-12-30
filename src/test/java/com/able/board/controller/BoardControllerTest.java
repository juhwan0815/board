package com.able.board.controller;

import com.able.board.dto.BoardCreateRequest;
import com.able.board.dto.BoardResponse;
import com.able.board.dto.BoardUpdateRequest;
import com.able.board.dto.ExceptionResponse;
import com.able.board.service.BoardService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(BoardController.class)
class BoardControllerTest {

    @MockBean
    private BoardService boardService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext wac,
               RestDocumentationContextProvider restDocumentationContextProvider) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .apply(documentationConfiguration(restDocumentationContextProvider)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();
    }

    @Test
    void exception() throws Exception {

        Map<String, String> errors = new HashMap<>();
        errors.put("title", "게시글 제목은 필수입니다.");

        ExceptionResponse exceptionResponse = new ExceptionResponse("잘못된 요청입니다.", errors);

        BoardCreateRequest boardCreateRequest = new BoardCreateRequest();
        boardCreateRequest.setContent("안녕");
        boardCreateRequest.setWriter("안녕");

        mockMvc.perform(post("/boards")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(boardCreateRequest))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)))
                .andDo(document("study/exception",
                        responseFields(
                                fieldWithPath("message").type(JsonFieldType.STRING).description("예외 메세지"),
                                fieldWithPath("attributes").type(JsonFieldType.OBJECT).description("예외 필드"),
                                fieldWithPath("attributes.title").type(JsonFieldType.STRING).description("필드 오류")
                        )
                ));
    }

    @Test
    void create() throws Exception {
        BoardCreateRequest boardCreateRequest = new BoardCreateRequest("안녕하세요", "여러분 ㅎㅇ여", "황철원");
        BoardResponse boardResponse = new BoardResponse(1L, "안녕하세요", "여러분 ㅎㅇ여", "황철원", LocalDateTime.now(), LocalDateTime.now());

        given(boardService.create(any()))
                .willReturn(boardResponse);

        mockMvc.perform(post("/boards")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(boardCreateRequest))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(boardResponse)))
                .andDo(document("board/create",
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("writer").type(JsonFieldType.STRING).description("작성자")
                        ),
                        responseFields(
                                fieldWithPath("boardId").type(JsonFieldType.NUMBER).description("게시글 ID"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("writer").type(JsonFieldType.STRING).description("작성자"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("작성일"),
                                fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("수정일")
                        )
                ));
    }

    @Test
    void update() throws Exception {
        BoardUpdateRequest boardUpdateRequest = new BoardUpdateRequest("안녕하세요", "여러분 ㅎㅇ여");
        BoardResponse boardResponse = new BoardResponse(1L, "안녕하세요", "여러분 ㅎㅇ여", "황철원", LocalDateTime.now(), LocalDateTime.now());

        given(boardService.update(any(), any()))
                .willReturn(boardResponse);

        mockMvc.perform(put("/boards/{boardId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(boardUpdateRequest))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(boardResponse)))
                .andDo(document("board/update",
                        pathParameters(
                                parameterWithName("boardId").description("게시글 ID")
                        ),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용")
                        ),
                        responseFields(
                                fieldWithPath("boardId").type(JsonFieldType.NUMBER).description("게시글 ID"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("writer").type(JsonFieldType.STRING).description("작성자"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("작성일"),
                                fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("수정일")
                        )
                ));
    }

    @Test
    void delete() throws Exception {
        willDoNothing()
                .given(boardService)
                .delete(any());

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/boards/{boardId}", 1L))
                .andExpect(status().isOk())
                .andDo(document("board/delete",
                        pathParameters(
                                parameterWithName("boardId").description("게시글 ID")
                        )
                ));
    }

    @Test
    void findAll() throws Exception {

        BoardResponse boardResponse = new BoardResponse(1L, "안녕하세요", "여러분 ㅎㅇ여", "황철원", LocalDateTime.now(), LocalDateTime.now());
        Page<BoardResponse> result = new PageImpl<>(Arrays.asList(boardResponse), PageRequest.of(0, 10), 1);

        given(boardService.findAll(any()))
                .willReturn(result);

        mockMvc.perform(get("/boards")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .param("size", "10")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)))
                .andDo(document("board/find",
                        requestParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 사이즈")
                        ),
                        responseFields(
                                fieldWithPath("content").type(JsonFieldType.ARRAY).description("조회 결과 배열"),
                                fieldWithPath("content.[].boardId").type(JsonFieldType.NUMBER).description("게시글 번호"),
                                fieldWithPath("content.[].title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("content.[].writer").type(JsonFieldType.STRING).description("작성자"),
                                fieldWithPath("content.[].createdAt").type(JsonFieldType.STRING).description("작성일"),
                                fieldWithPath("content.[].updatedAt").type(JsonFieldType.STRING).description("수정일"),
                                fieldWithPath("pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부 "),
                                fieldWithPath("pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬 여부"),
                                fieldWithPath("pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("값이 비었는지 여부"),
                                fieldWithPath("pageable.offset").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("pageable.pageNumber").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),
                                fieldWithPath("pageable.unpaged").type(JsonFieldType.BOOLEAN).description("비페이징 여부"),
                                fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("총 페이지 수"),
                                fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("총 결과 수"),
                                fieldWithPath("size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("number").type(JsonFieldType.NUMBER).description("페이지 번호"),
                                fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬여부"),
                                fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN).description("비정렬여부"),
                                fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN).description("값이 비었는지 여부"),
                                fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지 크기"),
                                fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("처음 페이지 여부"),
                                fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("값이 비었는지 여부")
                        )
                ));
    }

    @Test
    void findById() throws Exception {
        BoardResponse boardResponse = new BoardResponse(1L, "안녕하세요", "여러분 ㅎㅇ여", "황철원", LocalDateTime.now(), LocalDateTime.now());

        given(boardService.findById(any()))
                .willReturn(boardResponse);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/boards/{boardId}", 1L)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(boardResponse)))
                .andDo(document("board/findById",
                        pathParameters(
                                parameterWithName("boardId").description("게시글 ID")
                        ),
                        responseFields(
                                fieldWithPath("boardId").type(JsonFieldType.NUMBER).description("게시글 번호"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("writer").type(JsonFieldType.STRING).description("작성자"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("작성일"),
                                fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("수정일")
                        )
                ));
    }
}