package com.able.board.service;

import com.able.board.domain.Board;
import com.able.board.dto.BoardCreateRequest;
import com.able.board.dto.BoardResponse;
import com.able.board.dto.BoardUpdateRequest;
import com.able.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardServiceImpl implements BoardService{

    private final BoardRepository boardRepository;

    @Override
    @Transactional
    public BoardResponse create(BoardCreateRequest request) {
        Board board = Board.createBoard(request.getTitle(), request.getContent(), request.getWriter());
        boardRepository.save(board);
        return BoardResponse.from(board);
    }

    @Override
    @Transactional
    public BoardResponse update(Long boardId, BoardUpdateRequest request) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException());

        findBoard.changeTitleAndContent(request.getTitle(), request.getContent());

        return BoardResponse.from(findBoard);
    }

    @Override
    @Transactional
    public void delete(Long boardId) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException());

        boardRepository.delete(findBoard);
    }

    @Override
    public Page<BoardResponse> findAll(Pageable pageable) {
        Page<Board> boards = boardRepository.findAll(pageable);
        return boards.map(board -> BoardResponse.from(board));
    }

    @Override
    public BoardResponse findById(Long boardId) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException());
        return BoardResponse.from(findBoard);
    }
}
