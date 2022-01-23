package com.able.board.controller;

import com.able.board.dto.BoardCreateRequest;
import com.able.board.dto.BoardResponse;
import com.able.board.dto.BoardUpdateRequest;
import com.able.board.service.BoardService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final Environment env;

    @GetMapping
    public String healthCheck() {
        return env.getProperty("local.server.port");
    }

    @PostMapping("/boards")
    public ResponseEntity<BoardResponse> create(@RequestBody @Valid BoardCreateRequest request) {
        return ResponseEntity.ok(boardService.create(request));
    }

    @PutMapping("/boards/{boardId}")
    public ResponseEntity<BoardResponse> update(@PathVariable Long boardId,
                                                @RequestBody @Valid BoardUpdateRequest request) {
        return ResponseEntity.ok(boardService.update(boardId, request));
    }

    @DeleteMapping("/boards/{boardId}")
    public ResponseEntity<Void> delete(@PathVariable Long boardId) {
        boardService.delete(boardId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/boards")
    public ResponseEntity<Page<BoardResponse>> findAll(@PageableDefault(size = 10, page = 0) Pageable pageable) {
        return ResponseEntity.ok(boardService.findAll(pageable));
    }

    @GetMapping("/boards/{boardId}")
    public ResponseEntity<BoardResponse> findById(@PathVariable Long boardId) {
        return ResponseEntity.ok(boardService.findById(boardId));
    }
}
