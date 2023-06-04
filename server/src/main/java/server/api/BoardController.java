package server.api;

import commons.Board;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import server.service.AdminService;
import server.service.BoardService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Controller
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService boardService;
    private final ExecutorService executorService;
    private final AdminService adminService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    private List<CompletableFuture<List<Object>>> longPollFutures = new CopyOnWriteArrayList<>();

    public BoardController(BoardService boardService, AdminService adminService, SimpMessagingTemplate simpMessagingTemplate) {
        this.boardService = boardService;
        this.adminService = adminService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestHeader(HttpHeaders.AUTHORIZATION) String password) {
        if (adminService.checkPassword(password)) {
            return ResponseEntity.ok(boardService.getAll());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ArrayList<>());
        }
    }

    @PostMapping
    public ResponseEntity<?> createBoard(@RequestBody Board board) {
        return ResponseEntity.status(HttpStatus.CREATED).body(boardService.createBoard(board));
    }

    @PutMapping
    public ResponseEntity<?> updateBoard(@RequestBody Board board) {
        Board responseBoard = boardService.updateBoard(board);
        return ResponseEntity.ok(responseBoard);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(boardService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBoard(@PathVariable Integer id) {
        boardService.deleteById(id);
        simpMessagingTemplate.convertAndSend("/updates/board/" + id,
                new Board(-1, null, null, null, null, null, null, null));
        return ResponseEntity.ok().build();
    }


    @GetMapping("/long-poll-changes")
    public ResponseEntity<List<Object>> longPollForChanges(@RequestHeader(HttpHeaders.AUTHORIZATION) String password)
            throws InterruptedException, ExecutionException {

        if (!adminService.checkPassword(password))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ArrayList<>());

        CompletableFuture<List<Object>> future = new CompletableFuture<>();
        longPollFutures.add(future);

        // Submit the task to an executor service to run in a separate thread.
        executorService.submit(() -> {
            try {
                // Wait for new boards to become available.
                List<Object> changes = boardService.waitForChanges();

                for (CompletableFuture<List<Object>> longPollFuture : longPollFutures) {
                    longPollFuture.complete(changes);
                }
                // Clear the list of futures.
                longPollFutures.clear();

            } catch (InterruptedException e) {
                // If the thread is interrupted, cancel the future.
                future.cancel(true);
            }
        });

        try {
            // Get the result of the future if it completes successfully.
            List<Object> change = future.get();
            return new ResponseEntity<>(change, HttpStatus.OK);
        } catch (CancellationException e) {
            // If the future is cancelled due to a timeout, return a no content response.
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    /**
     * Endpoint for long polling for updates to a specific board.
     *
     * @param id the ID of the board to wait for updates on.
     * @return ResponseEntity containing the updated board and an HTTP status code.
     * @throws InterruptedException if the thread waiting for updates is interrupted.
     * @throws ExecutionException   if an error occurs while waiting for updates.
     */
    @GetMapping("/long-poll/{id}")
    public ResponseEntity<Board> longPollForBoardUpdate(@PathVariable Integer id) throws InterruptedException, ExecutionException {
        CompletableFuture<Board> future = new CompletableFuture<>();
        // Submit the task to an executor service to run in a separate thread.
        executorService.submit(() -> {
            try {
                // Wait for updates to the specified board to become available.
                Board board = boardService.waitForBoardUpdate(id);
                future.complete(board);
            } catch (InterruptedException e) {
                // If the thread is interrupted, cancel the future.
                future.cancel(true);
            }
        });

        try {
            // Get the result of the future if it completes successfully.
            Board board = future.get();
            return new ResponseEntity<>(board, HttpStatus.OK);
        } catch (CancellationException e) {
            // If the future is cancelled due to a timeout, return a no content response.
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @MessageMapping("/update/board/{id}")
    public void update(@DestinationVariable Integer id, @Payload Board board) {
        simpMessagingTemplate.convertAndSend("/updates/board/" + id, boardService.updateBoard(board));
    }

    /*
    When we delete a board, we want all the other clients currently on that board to be kicked of that board.
    that is why we are sending a board with id -1, this we catch on the client side and it handles the rest.
     */
    @MessageMapping("/delete/board/{boardId}")
    public void delete(@DestinationVariable Integer boardId, @Payload Integer id) {
        boardService.deleteById(id);
        simpMessagingTemplate.convertAndSend("/updates/board/" + boardId,
                new Board(-1, null, null, null, null, null, null, null));
    }
}


