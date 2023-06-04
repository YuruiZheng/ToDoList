package server.service;

import commons.Card;

import java.util.List;

public interface CardService {
    List<Card> getAll();
    Card createCard(Card card);
    Card updateCard(Card card);
    Card getById(Integer id);
    void deleteById(Integer id);
    List<Card> getCardsFromListById(Integer listId);

    void deleteAll();
}
