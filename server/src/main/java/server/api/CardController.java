package server.api;

import commons.Card;
import commons.CardTag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import server.service.BoardService;
import server.service.CardService;
import server.service.CardTagService;

@RestController
@RequestMapping("/api/card")
public class CardController {

    private final CardService cardService;
    private final BoardService boardService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    private final CardTagService cardTagService;

    public CardController(CardService cardService, BoardService boardService,
                          SimpMessagingTemplate simpMessagingTemplate,CardTagService cardTagService) {
        this.cardService = cardService;
        this.boardService = boardService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.cardTagService = cardTagService;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(cardService.getAll());
    }

    @GetMapping("/list/{listId}")
    public ResponseEntity<?> getCardsFromListById(@PathVariable("listId") Integer listId) {
        return ResponseEntity.ok(cardService.getCardsFromListById(listId));
    }

    @PostMapping
    public ResponseEntity<?> createCard(@RequestBody Card card) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cardService.createCard(card));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Card> getById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(cardService.getById(id));
    }

    @PutMapping()
    public ResponseEntity<?> updateCard(@RequestBody Card card) {
        return ResponseEntity.ok(cardService.updateCard(card));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCard(@PathVariable("id") Integer id) {
        cardService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<?> deleteAll() {
        cardService.deleteAll();
        return ResponseEntity.ok().build();
    }

    @MessageMapping("/create/card/{boardId}")
    public void create(@DestinationVariable Integer boardId, @Payload Card card) {
        cardService.createCard(card);
        simpMessagingTemplate.convertAndSend("/updates/board/" + boardId, boardService.getById(boardId));
    }

    @MessageMapping("/update/card/{boardId}")
    public void update(@DestinationVariable Integer boardId, @Payload Card card) {
        cardService.updateCard(card);
        simpMessagingTemplate.convertAndSend("/updates/board/" + boardId, boardService.getById(boardId));
        simpMessagingTemplate.convertAndSend("/updates/card/" + card.getId(), card);
    }

    @MessageMapping("/delete/card/{boardId}")
    public void delete(@DestinationVariable Integer boardId, @Payload Integer id) {
        cardService.deleteById(id);
        Object payload = boardService.getById(boardId);
        simpMessagingTemplate.convertAndSend("/updates/board/" + boardId, payload);
        simpMessagingTemplate.convertAndSend("/updates/card/" + id, new Card(-1, null, null, null, null, null));
    }

    @MessageMapping("/addTag/card/{boardId}")
    public void addTag(@DestinationVariable Integer boardId, @Payload CardTag cardTag)  {
        cardTagService.save(cardTag);
        Object payload = boardService.getById(boardId);
        simpMessagingTemplate.convertAndSend("/updates/board/" + boardId, payload);
        simpMessagingTemplate.convertAndSend("/updates/card/" + cardTag.getCardId(), cardService.getById(cardTag.getCardId()));
    }

    @MessageMapping("/deleteTag/card/{boardId}")
    public void deleteTag(@DestinationVariable Integer boardId, @Payload CardTag cardTag)  {
        cardTagService.deleteCardTagByCardIdAndTagId(cardTag.getCardId(),cardTag.getTagId());
        Object payload = boardService.getById(boardId);
        simpMessagingTemplate.convertAndSend("/updates/board/" + boardId, payload);
        simpMessagingTemplate.convertAndSend("/updates/card/" + cardTag.getCardId(), cardService.getById(cardTag.getCardId()));
    }


}
