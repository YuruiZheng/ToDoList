package server.database;

import commons.ToDoList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ToDoListRepository extends JpaRepository<ToDoList,Integer> {
    List<ToDoList> findAllListByBoardId(Integer boardId);
}
