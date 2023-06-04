package server.service.impl;

import commons.Card;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.database.CardRepository;
import server.service.CardService;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;

    public CardServiceImpl(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Override
    public List<Card> getAll() {
        return cardRepository.findAll();
    }

    /**
     * The card will always be added at the end of the list.
     */
    @Override
    public Card createCard(Card card) {
        Integer priority = cardRepository.getHighestPriorityByList(card.getListId());
        card.setPriority(priority == null ? 1 : priority + 1);
        return cardRepository.save(card);
    }

    @Override
    public Card updateCard(Card card) throws IllegalArgumentException {
        Card savedCard = cardRepository.findById(card.getId())
                .orElseThrow(() -> new IllegalArgumentException("No such card in the database"));
        if (!Objects.isNull(card.getPriority()))
            if (!Objects.equals(card.getPriority(), savedCard.getPriority()) || !Objects.equals(card.getListId(),savedCard.getListId())){
                cardRepository.incrementPriority(card.getPriority(), card.getListId());
            }
        savedCard.update(card);
        return cardRepository.save(savedCard);
    }

    @Override
    public Card getById(Integer id) throws IllegalArgumentException {
        return cardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No such card in the database"));
    }

    @Override
    public void deleteById(Integer id) throws IllegalArgumentException {
        if (!cardRepository.existsById(id)) throw new IllegalArgumentException("No such card in the database");
        cardRepository.deleteById(id);
    }

    @Override
    public List<Card> getCardsFromListById(Integer listId) {
        return cardRepository.findCardsByListIdOrderByPriority(listId);
    }

    @Override
    public void deleteAll() {
        cardRepository.deleteAll();
    }
}
