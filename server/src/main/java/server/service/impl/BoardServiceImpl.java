package server.service.impl;

import commons.Board;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.database.BoardRepository;
import server.service.BoardService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class BoardServiceImpl implements BoardService {

    private static final String NO_SUCH_BOARD = "Board does not exist";
    private final BoardRepository boardRepository;
    private final BlockingQueue<Board> newBoardQueue = new LinkedBlockingQueue<>();
    private final Map<Integer, Long> lastUpdateTimes = new HashMap<>();

    private final BlockingQueue<Object> changeQueue = new LinkedBlockingQueue<>();

    private final BlockingQueue<Board> addQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<Integer> deleteQueue = new LinkedBlockingQueue<>();



    public BoardServiceImpl(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Override
    public List<Board> getAll() {
        return boardRepository.findAll();
    }

    @Override
    public Board createBoard(Board board) {
        Board newlyAdded = boardRepository.save(board);
        changeQueue.add(board);
        return newlyAdded;
    }

    /**
     * updates a board
     *
     * @param board retrieves the board with the same id from the database and updates it, before saving it back
     * @throws IllegalArgumentException if the find by id fails.
     **/
    @Override
    public Board updateBoard(Board board) throws IllegalArgumentException {
        Board savedBoard = boardRepository.findById(board.getId())
                .orElseThrow(() -> new IllegalArgumentException(NO_SUCH_BOARD));
        board.setLastUpdate(savedBoard.getLastUpdate());
        savedBoard.applyUpdates(board);
        changeQueue.add(board);
        return boardRepository.save(savedBoard);
    }

    /**
     * gets a board by id
     *
     * @param id retrieves the board with the same id from the database and updates
     * @return returns the board with the id from input
     * @throws IllegalArgumentException if the find by id fails.
     **/
    @Override
    public Board getById(Integer id) throws IllegalArgumentException {
        return boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(NO_SUCH_BOARD));
    }

    @Transactional
    @Override
    public void deleteById(Integer id) throws IllegalArgumentException {
        boardRepository.deleteById(id);
        changeQueue.add(id);
    }


    /**
     * takes the first Object in the queue and returns it in a List
     * @return
     * @throws InterruptedException
     */
    public List<Object> waitForChanges() throws InterruptedException {
        Object change = changeQueue.take();

        return Collections.singletonList(change);
    }



    /**
     * Blocks the current thread until a new board is available in the newBoardQueue.
     *
     * @return List containing only the newly added Board object.
     * @throws InterruptedException if the current thread is interrupted while waiting for a new board.
     */
    public List<Board> waitForNewBoard() throws InterruptedException {
        Board newBoard = addQueue.take();

        // Return list containing only new board
        return Collections.singletonList(newBoard);
    }

    /**
     * Blocks the current thread until the board with the given id is updated.
     *
     * @param id the id of the board to wait for an update on.
     * @return the updated Board object.
     * @throws InterruptedException if the current thread is interrupted while waiting for an update.
     */
    public Board waitForBoardUpdate(Integer id) throws InterruptedException {
        while (true) {
            // Get the board with the given id
            Board board = boardRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException(NO_SUCH_BOARD));

            // Check if the board has been updated since the last time we checked
            if (board.getLastUpdate() > lastUpdateTimes.getOrDefault(id, 0L)) {
                // Update the last update time for this board
                lastUpdateTimes.put(id, board.getLastUpdate());
                return board;
            }

            // If the board has not been updated, wait for 1 second before trying again
            Thread.sleep(1000);
        }
    }


}
