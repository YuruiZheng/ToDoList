package server.service;

import commons.Subtask;

import java.util.List;

public interface SubtaskService {
    Subtask getById (Integer id);
    Subtask createSubtask(Subtask subtask);
    List<Subtask> getAll();
    List<Subtask> getByCardId(Integer id);
    Subtask updateSubtask(Subtask subtask);
    void deleteById(Integer id);
}
