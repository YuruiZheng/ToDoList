package server.service;

import commons.Board;

import java.util.List;

public interface BoardService{

    List<Board> getAll();
    Board createBoard(Board board);
    Board updateBoard(Board board);
    Board getById(Integer id);
    void deleteById(Integer id);
    List<Object> waitForChanges() throws InterruptedException;
    List<Board> waitForNewBoard() throws InterruptedException;
    Board waitForBoardUpdate(Integer id) throws InterruptedException;
}
