package server.service.impl;

import commons.Subtask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import server.database.SubtaskRepository;
import server.service.SubtaskService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SubtaskServiceImplTest {

    private static final Integer subId = 123;
    private static final Integer cardId = 1;
    private static final Subtask subtask = new Subtask(subId, cardId, "title1", false,1);
    private static final Subtask subtask1 = new Subtask(2, cardId, "title1", true,2);
    @Mock
    private static SubtaskRepository subtaskRepository;
    private static SubtaskService subtaskService;

    @BeforeEach
    void initialize() {
        subtaskService = new SubtaskServiceImpl(subtaskRepository);
    }


    @Test
    void getById() {
        Mockito.when(subtaskRepository.findById(subId)).thenReturn(Optional.of(subtask));
        assertEquals(subtask, subtaskService.getById(subId));
    }

    @Test
    void createSubtask() {
        Mockito.when(subtaskRepository.save(subtask)).thenReturn(subtask);
        assertEquals(subtask, subtaskService.createSubtask(subtask));
    }

    @Test
    void getAll() {
        Mockito.when(subtaskRepository.findAll()).thenReturn(List.of(subtask,subtask1));
        assertEquals(List.of(subtask,subtask1), subtaskService.getAll());
    }

    @Test
    void getByCardId() {
        Mockito.when(subtaskRepository.findSubtasksByCardIdOrderByPriority(cardId)).thenReturn(List.of(subtask,subtask1));
        assertEquals(List.of(subtask,subtask1), subtaskService.getByCardId(cardId));
    }

    @Test
    void updateSubtask() {
        Subtask toBeUpdated = new Subtask(subId,cardId,"title",true, 1);
        Mockito.when(subtaskRepository.findById(subId))
                .thenReturn(Optional.of(toBeUpdated));
        Mockito.when(subtaskRepository.save(subtask)).thenReturn(subtask);
        assertEquals(subtask, subtaskService.updateSubtask(subtask));
    }

    @Test
    void updateSubtaskDoesNotExist() {
        Mockito.when(subtaskRepository.findById(subId))
                .thenThrow(new IllegalArgumentException());
        assertThrows(IllegalArgumentException.class, ()->subtaskService.updateSubtask(subtask));
    }

    @Test
    void deleteById() {
        assertDoesNotThrow(()->subtaskRepository.deleteById(subId));
    }

    @Test
    void deleteByIdDoesNotExist() {
        Mockito.doThrow(new EmptyResultDataAccessException(0)).when(subtaskRepository).deleteById(subId);
        assertThrows(EmptyResultDataAccessException.class, ()->subtaskRepository.deleteById(subId));
    }
}