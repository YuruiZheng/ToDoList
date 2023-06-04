package server.service.impl;

import commons.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import server.database.TagRepository;
import server.service.TagService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TagServiceImplTest {

    private static final Tag tag = new Tag(1, 1, "myTitle", "000000");

    @Mock
    private static TagRepository tagRepository;

    private static TagService tagService;
    @BeforeEach
    void init() {
     tagService = new TagServiceImpl(tagRepository);
    }

    @Test
    void createTag() {
        Mockito.when(tagRepository.save(tag)).thenReturn(tag);
        assertEquals(tag, tagService.createTag(tag));
    }

    @Test
    void getAll() {
        Mockito.when(tagRepository.findAll()).thenReturn(List.of(tag));
        assertEquals(List.of(tag), tagService.getAll());
    }

    @Test
    void getByBoard() {
        Mockito.when(tagRepository.findAllByBoardId(tag.getBoardId()))
                .thenReturn(List.of(tag));
        assertEquals(List.of(tag), tagService.getByBoard(tag.getBoardId()));
    }

    @Test
    void updateTag() {
        Tag savedTag = new Tag(tag.getId(), tag.getBoardId(), "anotherTitle", "000012");
        Mockito.when(tagRepository.findById(tag.getId()))
                .thenReturn(Optional.of(savedTag));
        Mockito.when(tagRepository.save(tag)).thenReturn(tag);
        assertEquals(tag, tagService.updateTag(tag));
    }

    @Test
    void updateTagDoesNotExist() {
        Mockito.when(tagRepository.findById(tag.getId()))
                .thenThrow(new IllegalArgumentException());
        assertThrows(IllegalArgumentException.class, () -> tagService.updateTag(tag));
    }
    @Test
    void deleteById() {
        assertDoesNotThrow(() -> tagService.deleteById(tag.getId()));
    }

    @Test
    void deleteByIdDoesNotExist() {
        Mockito.doThrow(new EmptyResultDataAccessException(0)).when(tagRepository).deleteById(tag.getId());
        assertThrows(EmptyResultDataAccessException.class, () -> tagService.deleteById(tag.getId()));
    }
}