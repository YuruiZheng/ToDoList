package server.service;

import commons.CardTag;

public interface CardTagService {
    void deleteCardTagByCardIdAndTagId(Integer card,Integer tag);

    void save(CardTag cardTag);
}
