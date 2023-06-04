package server.database;

import commons.ColorPreset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColorPresetRepository extends JpaRepository<ColorPreset, Integer> {
    @Query(value = "SELECT * FROM COLOR_PRESETS WHERE BOARD_ID = ?1", nativeQuery = true)
    List<ColorPreset> findAllColorPresetsByBoardId(Integer boardId);
}
