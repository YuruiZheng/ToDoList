package server.service.impl;

import commons.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import server.database.BoardRepository;
import server.service.BoardService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BoardServiceImplTest {
    private static final Board board = new Board(1, "myBoard", "myPassword", "000000", "000000", "000000","ffffff", null);

    @Mock
    private static BoardRepository boardRepository;
    private static BoardService boardService;

    @BeforeEach
    private void init() {
        boardService = new BoardServiceImpl(boardRepository);
    }

    @Test
    void getAll() {
        Mockito.when(boardRepository.findAll()).thenReturn(List.of(board));
        assertEquals(List.of(board), boardService.getAll());
    }

    @Test
    void createBoard() {
        Mockito.when(boardRepository.save(board)).thenReturn(board);
        assertEquals(board, boardService.createBoard(board));
    }

    @Test
    void updateBoard() {
        Board toBeUpdated = board;
        toBeUpdated.setBackground("000003");

        Mockito.when(boardRepository.findById(toBeUpdated.getId()))
                .thenReturn(Optional.of(board));
        Mockito.when(boardRepository.save(board))
                .thenReturn(toBeUpdated);

        Board updatedBoard = boardService.updateBoard(toBeUpdated);
        board.setLastUpdate(updatedBoard.getLastUpdate() + 1);
        assertEquals(board, toBeUpdated);
    }

    @Test
    void getById() {
        Mockito.when(boardRepository.findById(board.getId())).thenReturn(Optional.of(board));
        assertEquals(board, boardService.getById(board.getId()));
    }

    @Test
    void getByIdDoesNotExist() {
        assertThrows(IllegalArgumentException.class, () -> boardService.getById(board.getId() + 100));
    }

    @Test
    void deleteById() {
        assertDoesNotThrow(() -> boardService.deleteById(board.getId()));
    }

    @Test
    void deleteByIdDoesNotExist() {
        Mockito.doThrow(new EmptyResultDataAccessException(0)).when(boardRepository).deleteById(board.getId());
        assertThrows(EmptyResultDataAccessException.class, () -> boardService.deleteById(board.getId()));
    }

    @Test
    void waitForUpdateBoard() throws InterruptedException {
        board.setLastUpdate(1);
        Mockito.when(boardRepository.findById(board.getId())).thenReturn(Optional.of(board));
        assertEquals(board, boardService.waitForBoardUpdate(board.getId()));
    }

    @Test
    void waitForUpdateBoardUnknownId() throws InterruptedException {
        assertThrows(IllegalArgumentException.class, () -> boardService.waitForBoardUpdate(board.getId() + 99));
    }
}