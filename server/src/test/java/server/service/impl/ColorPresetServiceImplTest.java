package server.service.impl;

import commons.ColorPreset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import server.database.ColorPresetRepository;
import server.service.ColorPresetService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ColorPresetServiceImplTest {
    private static final Integer boardId = 1;
    private static final Integer cpId = 1;
    private static final ColorPreset cp1 = new ColorPreset(cpId, boardId, "cp1", "000000", "000000");
    private static final ColorPreset cp2 = new ColorPreset(2, boardId, "cp1", "ffffff", "ffffff");

    @Mock
    private static ColorPresetRepository colorPresetRepository;

    private static ColorPresetService colorPresetService;

    @BeforeEach
    void initialize() {
        colorPresetService = new ColorPresetServiceImpl(colorPresetRepository);
    }

    @Test
    void getById() {
        Mockito.when(colorPresetRepository.findById(cpId)).thenReturn(Optional.of(cp1));
        assertEquals(cp1, colorPresetService.getById(cpId));
    }

    @Test
    void getAll() {
        Mockito.when(colorPresetRepository.findAll()).thenReturn(List.of(cp1, cp2));
        assertEquals(List.of(cp1, cp2), colorPresetService.getAll());
    }

    @Test
    void create() {
        Mockito.when(colorPresetRepository.save(cp1)).thenReturn(cp1);
        assertEquals(cp1, colorPresetService.createColorPreset(cp1));
    }

    @Test
    void update() {
        Mockito.when(colorPresetRepository.existsById(cp1.getId()))
                .thenReturn(true);
        ColorPreset cp = new ColorPreset();
        Mockito.when(colorPresetRepository.save(cp))
                .thenReturn(cp);
        cp.setId(cp1.getId());
        Mockito.when(colorPresetRepository.findById(cp1.getId()))
                .thenReturn(Optional.of(cp));
        assertEquals(cp, colorPresetService.updateColorPreset(cp));
    }

    @Test
    void deleteById() {
        Mockito.when(colorPresetRepository.existsById(cpId))
                .thenReturn(true);
        assertDoesNotThrow(()->colorPresetRepository.deleteById(cpId));
    }

    @Test
    void findAllColorPresetsFromBoardById() {
        Mockito.when(colorPresetService.findAllColorPresetsFromBoardById(1))
                .thenReturn(List.of(cp1, cp2));
        assertEquals(List.of(cp1, cp2), colorPresetService.findAllColorPresetsFromBoardById(1));
    }
}
