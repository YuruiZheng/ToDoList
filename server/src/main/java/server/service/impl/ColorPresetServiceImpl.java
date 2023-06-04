package server.service.impl;

import commons.ColorPreset;
import org.springframework.stereotype.Service;
import server.database.ColorPresetRepository;
import server.service.ColorPresetService;

import java.util.List;

@Service
public class ColorPresetServiceImpl implements ColorPresetService {
    private final ColorPresetRepository colorPresetRepository;

    public ColorPresetServiceImpl(ColorPresetRepository colorPresetRepository) {
        this.colorPresetRepository = colorPresetRepository;
    }

    @Override
    public List<ColorPreset> getAll() {
        return colorPresetRepository.findAll();
    }

    @Override
    public ColorPreset createColorPreset(ColorPreset cp) {
        return colorPresetRepository.save(cp);
    }

    @Override
    public ColorPreset updateColorPreset(ColorPreset cp) throws IllegalArgumentException {
        ColorPreset saved = colorPresetRepository.findById(cp.getId())
                .orElseThrow(() -> new IllegalArgumentException("No such color preset in the database"));
        saved.update(cp);
        return colorPresetRepository.save(saved);
    }

    @Override
    public ColorPreset getById(Integer id) throws IllegalArgumentException{
        return colorPresetRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No such color preset in the database"));
    }

    @Override
    public void deleteById(Integer id) throws IllegalArgumentException {
        if(!colorPresetRepository.existsById(id)) throw new IllegalArgumentException("No such color preset exists in the database");
        colorPresetRepository.deleteById(id);
    }

    @Override
    public List<ColorPreset> findAllColorPresetsFromBoardById(Integer boardId) {
        return colorPresetRepository.findAllColorPresetsByBoardId(boardId);
    }

    @Override
    public void deleteAll() {
        colorPresetRepository.deleteAll();
    }
}
