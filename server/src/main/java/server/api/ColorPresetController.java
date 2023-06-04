package server.api;

import commons.ColorPreset;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import server.service.BoardService;
import server.service.ColorPresetService;

import java.util.List;

@RestController
@RequestMapping("/api/colorpreset")
public class ColorPresetController {
    private final BoardService boardService;
    private final ColorPresetService colorPresetService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public ColorPresetController(BoardService boardService, ColorPresetService colorPresetService, SimpMessagingTemplate simpMessagingTemplate) {
        this.boardService = boardService;
        this.colorPresetService = colorPresetService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @PostMapping()
    public ResponseEntity<?> createColorPreset(@RequestBody ColorPreset colorPreset) {
        return ResponseEntity.ok(colorPresetService.createColorPreset(colorPreset));
    }

    @GetMapping("/board/{boardId}")
    public ResponseEntity<?> getColorPresetsFromBoardById(@PathVariable("boardId") Integer boardId) {
        return ResponseEntity.ok(colorPresetService.findAllColorPresetsFromBoardById(boardId));
    }

    @MessageMapping("/create/colorpreset/{boardId}")
    public void create(@DestinationVariable Integer boardId, @Payload ColorPreset colorPreset) {
        colorPresetService.createColorPreset(colorPreset);
        simpMessagingTemplate.convertAndSend("/updates/board/" + boardId, boardService.getById(boardId));
    }

    @MessageMapping("/update/colorpreset/{boardId}")
    public void update(@DestinationVariable Integer boardId, @Payload ColorPreset colorPreset) {
        colorPresetService.updateColorPreset(colorPreset);
        simpMessagingTemplate.convertAndSend("/updates/board/" + boardId, boardService.getById(boardId));
    }

    @MessageMapping("/delete/colorpreset/{boardId}")
    public void delete(@DestinationVariable Integer boardId, @Payload Integer id) {
        colorPresetService.deleteById(id);
        simpMessagingTemplate.convertAndSend("/updates/board/" + boardId, boardService.getById(boardId));
    }

    @MessageMapping("/delete-multiple/colorpreset/{boardId}")
    public void delete(@DestinationVariable Integer boardId, @Payload List<Integer> ids) {
        for(Integer id : ids) {
            colorPresetService.deleteById(id);
        }
        //no need to send updates back as the board will be updated by another websocket request
    }

    @MessageMapping("/update-multiple/colorpreset/{boardId}")
    public void update(@DestinationVariable Integer boardId, @Payload List<ColorPreset> colorPresets) {
        for(ColorPreset colorPreset : colorPresets) {
            colorPresetService.updateColorPreset(colorPreset);
        }
        //no need to send updates back as the board will be updated by another websocket request
    }
}
