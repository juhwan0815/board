package com.able.board.service;

import com.able.board.dto.BoardCreateRequest;
import com.able.board.dto.BoardResponse;
import com.able.board.dto.BoardUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardService {

    BoardResponse create(BoardCreateRequest request);

    BoardResponse update(Long boardId, BoardUpdateRequest request);

    void delete(Long boardId);

    Page<BoardResponse> findAll(Pageable pageable);

    BoardResponse findById(Long boardId);
}
