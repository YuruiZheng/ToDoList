package server.service.impl;

import commons.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import server.database.CardRepository;
import server.service.CardService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CardServiceImplTest {
    private static final Integer cardId = 123;
    private static final Card card = new Card(cardId, 1, "title1", "desc","000123", 2);
    private static final Card card1 = new Card(2, 1, "title2", "dsc", "000122", 1);
    @Mock
    private static CardRepository cardRepository;
    private static CardService cardService;

    @BeforeEach
    void initialize() {
        cardService = new CardServiceImpl(cardRepository);
    }

    @Test
    void getAll() {
        Mockito.when(cardRepository.findAll())
                .thenReturn(List.of(card));
        assertEquals(List.of(card), cardService.getAll());
    }

    @Test
    void createCard() {
        Mockito.when(cardRepository.save(card))
                .thenReturn(card);
        assertEquals(card, cardService.createCard(card));
    }

    @Test
    void updateCard() {
        Mockito.when(cardRepository.existsById(cardId))
                .thenReturn(true);
        Mockito.when(cardRepository.save(card))
                .thenReturn(card);
        Card cardForUpdate = new Card(cardId,1,"title2","desc2","012345", 1);
        Mockito.when(cardRepository.findById(cardId))
                .thenReturn(Optional.of(cardForUpdate));
        assertEquals(card, cardService.updateCard(card));
    }

    @Test
    void getById() {
        Mockito.when(cardRepository.existsById(cardId))
                .thenReturn(true);
        Mockito.when(cardRepository.findById(cardId))
                .thenReturn(Optional.of(card));
        assertEquals(card, cardService.getById(cardId));
    }

    @Test
    void deleteById() {
        Mockito.when(cardRepository.existsById(cardId))
                .thenReturn(true);
        assertDoesNotThrow(()->cardService.deleteById(cardId));
    }

    @Test
    void deleteByNonexistentId(){
        Mockito.doThrow(new EmptyResultDataAccessException(0)).when(cardRepository)
                .deleteById(cardId);
        assertThrows(IllegalArgumentException.class, () -> cardService.deleteById(cardId));
    }


    @Test
    void getCardsFromListById() {
        Mockito.when(cardRepository.findCardsByListIdOrderByPriority(1))
                .thenReturn(List.of(card, card1));
        assertEquals(List.of(card, card1), cardService.getCardsFromListById(1));
    }

    @Test
    void deleteAll() {
        assertDoesNotThrow(() -> cardService.deleteAll());
    }
}