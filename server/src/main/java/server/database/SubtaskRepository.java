package server.database;

import commons.Subtask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubtaskRepository extends JpaRepository<Subtask, Integer> {

    List<Subtask> findSubtasksByCardIdOrderByPriority(Integer cardId);
    @Query(value = "SELECT MAX(priority) FROM SUBTASK WHERE card_id = ?1", nativeQuery = true)
    Integer getHighestPriorityByCard(Integer cardId);

    @Modifying
    @Query(value = "UPDATE SUBTASK SET priority = priority + 1 WHERE priority >= ?1 AND card_id = ?2", nativeQuery = true)
    void incrementPriority(Integer priority, Integer cardId);
}
