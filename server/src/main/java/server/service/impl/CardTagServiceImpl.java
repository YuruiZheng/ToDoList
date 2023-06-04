package server.service.impl;

import commons.CardTag;
import org.springframework.stereotype.Service;
import server.database.CardTagRepository;
import server.service.CardTagService;

import javax.transaction.Transactional;

@Service
public class CardTagServiceImpl implements CardTagService {
    private final CardTagRepository cardTagRepository;
    public CardTagServiceImpl(CardTagRepository cardTagRepository) {
        this.cardTagRepository = cardTagRepository;
    }


    @Override
    public void save(CardTag cardTag) {
        cardTagRepository.save(cardTag);
    }
    @Transactional
    @Override
    public void deleteCardTagByCardIdAndTagId(Integer cardId, Integer tagId) {
        cardTagRepository.deleteCardTagByCardIdAndTagId(cardId,tagId);
    }
}
