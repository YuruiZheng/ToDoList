package server.service.impl;

import commons.ToDoList;
import org.springframework.stereotype.Service;
import server.database.ToDoListRepository;
import server.service.ToDoListService;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ToDoListServiceImpl implements ToDoListService {

    private final ToDoListRepository toDoListRepository;

    public ToDoListServiceImpl(ToDoListRepository toDoListRepository) {
        this.toDoListRepository = toDoListRepository;
    }

    public java.util.List<ToDoList> getAllLists() {
        return toDoListRepository.findAll();
    }

    public ToDoList createList(ToDoList list) {
        return toDoListRepository.save(list);
    }

    public ToDoList updateList(ToDoList list) throws IllegalArgumentException {
        ToDoList updateList = toDoListRepository.findById(list.getId())
                .orElseThrow(() -> new IllegalArgumentException("There does not exist a List with id: " + list.getId()));
        updateList.updateList(list);
        return toDoListRepository.save(updateList);
    }

    public ToDoList getListById(Integer id) throws IllegalArgumentException {
        return toDoListRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("There does not exist a List with id: " + id));

    }

    @Transactional
    public void deleteListById(Integer id) {
        try {
            toDoListRepository.deleteById(id);
        } catch (Exception e) {
            throw new IllegalArgumentException("Couldn't delete List");
        }
    }

    public List<ToDoList> getAllListsOnBoard(Integer boardId) {
        return toDoListRepository.findAllListByBoardId(boardId);
    }


}
