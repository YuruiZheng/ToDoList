package server.database;

import commons.CardTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardTagRepository  extends JpaRepository<CardTag, Integer> {

    void deleteCardTagByCardIdAndTagId(Integer cardId,Integer tagId);
}
