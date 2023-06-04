package server.api;

import commons.Subtask;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import server.service.BoardService;
import server.service.CardService;
import server.service.SubtaskService;

@RestController
@RequestMapping("/api/subtask")
public class SubtaskController {
    private final SubtaskService subtaskService;
    private final BoardService boardService;
    private final CardService cardService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public SubtaskController(SubtaskService subtaskService, BoardService boardService,
                             CardService cardService,
                             SimpMessagingTemplate simpMessagingTemplate) {
        this.subtaskService = subtaskService;
        this.boardService = boardService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.cardService = cardService;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(subtaskService.getAll());
    }

    @GetMapping("/card/{cardId}")
    public ResponseEntity<?> getSubtasksFromCard(@PathVariable("cardId") Integer cardId) {
        return ResponseEntity.ok(subtaskService.getByCardId(cardId));
    }

    @PostMapping
    public ResponseEntity<?> createSubtask(@RequestBody Subtask subtask) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subtaskService.createSubtask(subtask));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(subtaskService.getById(id));
    }

    @PutMapping()
    public ResponseEntity<?> updateSubtask(@RequestBody Subtask subtask) {
        return ResponseEntity.ok(subtaskService.updateSubtask(subtask));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubtask(@PathVariable("id") Integer id) {
        subtaskService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @MessageMapping("/create/subtask/{boardId}")
    public void create(@DestinationVariable Integer boardId, @Payload Subtask subtask) {
        subtaskService.createSubtask(subtask);
        simpMessagingTemplate.convertAndSend("/updates/card/" + subtask.getCardId(), cardService.getById(subtask.getCardId()));
        simpMessagingTemplate.convertAndSend("/updates/board/" + boardId,
                boardService.getById(boardId));
    }

    @MessageMapping("/update/subtask/{boardId}")
    public void update(@DestinationVariable Integer boardId, @Payload Subtask subtask) {
        subtaskService.updateSubtask(subtask);
        simpMessagingTemplate.convertAndSend("/updates/card/" + subtask.getCardId(), cardService.getById(subtask.getCardId()));
        simpMessagingTemplate.convertAndSend("/updates/board/" + boardId,
                boardService.getById(boardId));
    }

    @MessageMapping("/delete/subtask/{boardId}")
    public void delete(@DestinationVariable Integer boardId, @Payload Integer id) {
        int cardId = subtaskService.getById(id).getCardId();
        subtaskService.deleteById(id);
        simpMessagingTemplate.convertAndSend("/updates/card/" + cardId, cardService.getById(cardId));
        simpMessagingTemplate.convertAndSend("/updates/board/" + boardId,
                boardService.getById(boardId));
    }

}
