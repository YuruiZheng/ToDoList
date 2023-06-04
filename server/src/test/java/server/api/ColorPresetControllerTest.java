package server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.ColorPreset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import server.service.ColorPresetService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class ColorPresetControllerTest {

    private static final Integer boardId = 1;
    private static final Integer cpId = 1;
    private static final ColorPreset cp1 = new ColorPreset(cpId, boardId, "cp1", "000000", "000000");
    private static final ColorPreset cp2 = new ColorPreset(2, boardId, "cp1", "ffffff", "ffffff");
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private static ColorPresetService colorPresetService;
    @Autowired
    private WebApplicationContext webApplicationContext;
    private static MockMvc mockMvc;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void getByBoardId() throws Exception{
        Mockito.when(colorPresetService.findAllColorPresetsFromBoardById(1))
                .thenReturn(List.of(cp1,cp2));
        MvcResult result = mockMvc.perform(get("/api/colorpreset/board/1")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(objectMapper.writeValueAsString(List.of(cp1,cp2)),
                result.getResponse().getContentAsString());
    }

    @Test
    void create() throws Exception{
        ColorPreset cp = new ColorPreset();
        cp.setId(cp1.getId());
        Mockito.when(colorPresetService.createColorPreset(cp)).thenReturn(cp1);

        MvcResult result = mockMvc.perform(post("/api/colorpreset")
                        .content(objectMapper.writeValueAsString(cp))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        assertEquals(objectMapper.writeValueAsString(cp1),
                result.getResponse().getContentAsString());
    }

}
