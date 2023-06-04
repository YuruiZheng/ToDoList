package server.api;

import commons.ToDoList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import server.service.BoardService;
import server.service.ToDoListService;

@RestController
@RequestMapping("/api/List")
public class ToDoListController {
    private final ToDoListService toDoListService;
    private final BoardService boardService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public ToDoListController(ToDoListService toDoListService, BoardService boardService,
                              SimpMessagingTemplate simpMessagingTemplate) {
        this.toDoListService = toDoListService;
        this.boardService = boardService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @PostMapping
    public ResponseEntity<?> createList(@RequestBody ToDoList list) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toDoListService.createList(list));
    }

    @PutMapping
    public ResponseEntity<?> updateList(@RequestBody ToDoList list) {
        return ResponseEntity.ok(toDoListService.updateList(list));
    }

    @GetMapping("/{listId}")
    public ResponseEntity<?> getListById(@PathVariable Integer listId) {
        return ResponseEntity.ok(toDoListService.getListById(listId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteListById(@PathVariable Integer id) {
        toDoListService.deleteListById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/board/{boardId}")
    public ResponseEntity<?> getAllListsOnBoard(@PathVariable Integer boardId) {
        return ResponseEntity.ok(toDoListService.getAllListsOnBoard(boardId));
    }

    @GetMapping
    public ResponseEntity<?> getAllList() {
        return ResponseEntity.ok(toDoListService.getAllLists());
    }

    @MessageMapping("/create/list/{boardId}")
    public void create(@DestinationVariable Integer boardId, @Payload ToDoList list) {
        toDoListService.createList(list);
        simpMessagingTemplate.convertAndSend("/updates/board/" + boardId,
                boardService.getById(boardId));
    }

    @MessageMapping("/update/list/{boardId}")
    public void update(@DestinationVariable Integer boardId, @Payload ToDoList list) {
        toDoListService.updateList(list);
        simpMessagingTemplate.convertAndSend("/updates/board/" + boardId,
                boardService.getById(boardId));
    }

    @MessageMapping("/delete/list/{boardId}")
    public void delete(@DestinationVariable Integer boardId, @Payload Integer id) {
        toDoListService.deleteListById(id);
        simpMessagingTemplate.convertAndSend("/updates/board/" + boardId,
                boardService.getById(boardId));
    }
}
