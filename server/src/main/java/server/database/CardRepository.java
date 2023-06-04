package server.database;

import commons.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Integer> {
    List<Card> findCardsByListIdOrderByPriority(Integer listId);

    @Query(value = "SELECT MAX(priority) FROM CARD WHERE list_id = ?1", nativeQuery = true)
    Integer getHighestPriorityByList(Integer listId);

    @Modifying
    @Query(value = "UPDATE CARD SET priority = priority + 1 WHERE priority >= ?1 AND list_id = ?2", nativeQuery = true)
    void incrementPriority(Integer priority, Integer listId);
}
