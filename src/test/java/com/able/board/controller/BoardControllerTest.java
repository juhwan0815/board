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
        errors.put("title", "????????? ????????? ???????????????.");

        ExceptionResponse exceptionResponse = new ExceptionResponse("????????? ???????????????.", errors);

        BoardCreateRequest boardCreateRequest = new BoardCreateRequest();
        boardCreateRequest.setContent("??????");
        boardCreateRequest.setWriter("??????");

        mockMvc.perform(post("/boards")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(boardCreateRequest))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(objectMapper.writeValueAsString(exceptionResponse)))
                .andDo(document("study/exception",
                        responseFields(
                                fieldWithPath("message").type(JsonFieldType.STRING).description("?????? ?????????"),
                                fieldWithPath("attributes").type(JsonFieldType.OBJECT).description("?????? ??????"),
                                fieldWithPath("attributes.title").type(JsonFieldType.STRING).description("?????? ??????")
                        )
                ));
    }

    @Test
    void create() throws Exception {
        BoardCreateRequest boardCreateRequest = new BoardCreateRequest("???????????????", "????????? ?????????", "?????????");
        BoardResponse boardResponse = new BoardResponse(1L, "???????????????", "????????? ?????????", "?????????", LocalDateTime.now(), LocalDateTime.now());

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
                                fieldWithPath("title").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("writer").type(JsonFieldType.STRING).description("?????????")
                        ),
                        responseFields(
                                fieldWithPath("boardId").type(JsonFieldType.NUMBER).description("????????? ID"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("writer").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("?????????")
                        )
                ));
    }

    @Test
    void update() throws Exception {
        BoardUpdateRequest boardUpdateRequest = new BoardUpdateRequest("???????????????", "????????? ?????????");
        BoardResponse boardResponse = new BoardResponse(1L, "???????????????", "????????? ?????????", "?????????", LocalDateTime.now(), LocalDateTime.now());

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
                                parameterWithName("boardId").description("????????? ID")
                        ),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("??????")
                        ),
                        responseFields(
                                fieldWithPath("boardId").type(JsonFieldType.NUMBER).description("????????? ID"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("writer").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("?????????")
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
                                parameterWithName("boardId").description("????????? ID")
                        )
                ));
    }

    @Test
    void findAll() throws Exception {

        BoardResponse boardResponse = new BoardResponse(1L, "???????????????", "????????? ?????????", "?????????", LocalDateTime.now(), LocalDateTime.now());
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
                                parameterWithName("page").description("????????? ??????"),
                                parameterWithName("size").description("????????? ?????????")
                        ),
                        responseFields(
                                fieldWithPath("content").type(JsonFieldType.ARRAY).description("?????? ?????? ??????"),
                                fieldWithPath("content.[].boardId").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("content.[].title").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("content.[].content").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("content.[].writer").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("content.[].createdAt").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("content.[].updatedAt").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("?????? ?????? "),
                                fieldWithPath("pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("????????? ??????"),
                                fieldWithPath("pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("?????? ???????????? ??????"),
                                fieldWithPath("pageable.offset").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("pageable.pageNumber").type(JsonFieldType.NUMBER).description("?????? ????????? ??????"),
                                fieldWithPath("pageable.pageSize").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("pageable.paged").type(JsonFieldType.BOOLEAN).description("????????? ??????"),
                                fieldWithPath("pageable.unpaged").type(JsonFieldType.BOOLEAN).description("???????????? ??????"),
                                fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("????????? ????????? ??????"),
                                fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("??? ????????? ???"),
                                fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("??? ?????? ???"),
                                fieldWithPath("size").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("number").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN).description("????????????"),
                                fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN).description("???????????????"),
                                fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN).description("?????? ???????????? ??????"),
                                fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("?????? ????????? ??????"),
                                fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("?????? ????????? ??????"),
                                fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("?????? ???????????? ??????")
                        )
                ));
    }

    @Test
    void findById() throws Exception {
        BoardResponse boardResponse = new BoardResponse(1L, "???????????????", "????????? ?????????", "?????????", LocalDateTime.now(), LocalDateTime.now());

        given(boardService.findById(any()))
                .willReturn(boardResponse);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/boards/{boardId}", 1L)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(boardResponse)))
                .andDo(document("board/findById",
                        pathParameters(
                                parameterWithName("boardId").description("????????? ID")
                        ),
                        responseFields(
                                fieldWithPath("boardId").type(JsonFieldType.NUMBER).description("????????? ??????"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("??????"),
                                fieldWithPath("writer").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("?????????"),
                                fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("?????????")
                        )
                ));
    }
}