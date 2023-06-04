package server.service.impl;

import commons.Subtask;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.database.SubtaskRepository;
import server.service.SubtaskService;

import java.util.List;
import java.util.Objects;

@Transactional
@Service
public class SubtaskServiceImpl implements SubtaskService {

    private final SubtaskRepository subtaskRepository;

    public SubtaskServiceImpl(SubtaskRepository subtaskRepository) {
        this.subtaskRepository = subtaskRepository;
    }

    @Override
    public Subtask getById(Integer id) throws IllegalArgumentException {
        return subtaskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subtask not found"));
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Integer priority = subtaskRepository.getHighestPriorityByCard(subtask.getCardId());
        subtask.setPriority(priority == null ? 1 : priority + 1);
        return subtaskRepository.save(subtask);
    }

    @Override
    public List<Subtask> getAll() {
        return subtaskRepository.findAll();
    }

    @Override
    public List<Subtask> getByCardId(Integer id) {
        return subtaskRepository.findSubtasksByCardIdOrderByPriority(id);
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) throws IllegalArgumentException {
        Subtask savedSubtask = subtaskRepository.findById(subtask.getId())
                .orElseThrow(() -> new IllegalArgumentException("Subtask not found"));
        if (!Objects.isNull(subtask.getPriority()) && !Objects.equals(subtask.getPriority(), savedSubtask.getPriority()))
            subtaskRepository.incrementPriority(subtask.getPriority(), subtask.getCardId());
        savedSubtask.update(subtask);
        return subtaskRepository.save(savedSubtask);
    }

    @Override
    public void deleteById(Integer id) {
        subtaskRepository.deleteById(id);
    }
}
