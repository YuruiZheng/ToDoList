package server.service;

import commons.ColorPreset;

import java.util.List;

public interface ColorPresetService {
    List<ColorPreset> getAll();
    ColorPreset createColorPreset(ColorPreset cp);
    ColorPreset updateColorPreset(ColorPreset cp);
    ColorPreset getById(Integer id);
    void deleteById(Integer id);
    List<ColorPreset> findAllColorPresetsFromBoardById(Integer boardId);
    void deleteAll();
}
