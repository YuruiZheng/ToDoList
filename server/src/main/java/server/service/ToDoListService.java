package server.service;

import commons.ToDoList;

import java.util.List;

public interface ToDoListService {
    java.util.List<ToDoList> getAllLists();
    ToDoList createList(ToDoList list);
    ToDoList updateList(ToDoList list);
    ToDoList getListById(Integer id);
    void deleteListById(Integer id);
    List<ToDoList> getAllListsOnBoard(Integer boardId);
}
