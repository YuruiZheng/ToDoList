package server.api;


import commons.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import server.service.BoardService;
import server.service.TagService;

@RestController
@RequestMapping("/api/tag")
public class TagController {
    private final TagService tagService;
    private final BoardService boardService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public TagController(TagService tagService, BoardService boardService,
                         SimpMessagingTemplate simpMessagingTemplate) {
        this.tagService = tagService;
        this.boardService = boardService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @PostMapping
    public ResponseEntity<?> createTag(@RequestBody Tag tag) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tagService.createTag(tag));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(tagService.getAll());
    }

    @GetMapping("/board/{id}")
    public ResponseEntity<?> getByBoard(@PathVariable Integer id) {
        return ResponseEntity.ok(tagService.getByBoard(id));
    }

    @PutMapping
    public ResponseEntity<?> updateTag(@RequestBody Tag tag) {
        return ResponseEntity.ok(tagService.updateTag(tag));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Integer id) {
        tagService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @MessageMapping("/create/tag/{boardId}")
    public void create(@DestinationVariable Integer boardId, @Payload Tag tag) {
        tagService.createTag(tag);
        simpMessagingTemplate.convertAndSend("/updates/board/" + boardId,
                boardService.getById(boardId));
    }

    @MessageMapping("/update/tag/{boardId}")
    public void update(@DestinationVariable Integer boardId, @Payload Tag tag) {
        tagService.updateTag(tag);
        simpMessagingTemplate.convertAndSend("/updates/board/" + boardId,
                boardService.getById(boardId));
    }

    @MessageMapping("/delete/tag/{boardId}")
    public void delete(@DestinationVariable Integer boardId, @Payload Integer id) {
        tagService.deleteById(id);
        simpMessagingTemplate.convertAndSend("/updates/board/" + boardId,
                boardService.getById(boardId));
    }
}
