package server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Subtask;
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
import server.service.SubtaskService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SubtaskControllerTest {
    private static final Integer subId = 1;
    private static final Integer cardId = 1;
    private static final Subtask subtask = new Subtask(subId, cardId, "title1", false);
    private static final Subtask subtask1 = new Subtask(2, cardId, "title", false);
    private static final Subtask updatedSubtask = new Subtask(subId, 2, "title1", true);
    private static final Subtask noIdSubtask = new Subtask(null, 2, "title_", false);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @MockBean
    private static SubtaskService subtaskService;
    private static MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void getAll() throws Exception {
        Mockito.when(subtaskService.getAll()).thenReturn(List.of(subtask));
        MvcResult result = mockMvc.perform(get("/api/subtask")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(objectMapper.writeValueAsString(List.of(subtask)),
                result.getResponse().getContentAsString());
    }

    @Test
    void getSubtasksFromCard() throws Exception {
        Mockito.when(subtaskService.getByCardId(cardId))
                .thenReturn(List.of(subtask, subtask1));
        MvcResult result = mockMvc.perform(get("/api/subtask/card/" + cardId)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(objectMapper.writeValueAsString(List.of(subtask, subtask1)),
                result.getResponse().getContentAsString());
    }

    @Test
    void createSubtask() throws Exception {
        Mockito.when(subtaskService.createSubtask(subtask))
                .thenReturn(subtask);
        MvcResult result = mockMvc.perform(post("/api/subtask")
                        .content(objectMapper.writeValueAsString(subtask))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
        assertEquals(objectMapper.writeValueAsString(subtask),
                result.getResponse().getContentAsString());
    }

    @Test
    void getById() throws Exception {
        Mockito.when(subtaskService.getById(subId))
                .thenReturn(subtask);
        MvcResult result = mockMvc.perform(get("/api/subtask/" + subId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(objectMapper.writeValueAsString(subtask),
                result.getResponse().getContentAsString());
    }

    @Test
    void getByIdDoesNotExist() throws Exception {
        Mockito.doThrow(new IllegalArgumentException()).when(subtaskService).getById(subId);
        mockMvc.perform(get("/api/subtask/" + cardId))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateSubtask() throws Exception {
        Mockito.when(subtaskService.updateSubtask(updatedSubtask))
                .thenReturn(updatedSubtask);
        mockMvc.perform(put("/api/subtask/")
                        .content(objectMapper.writeValueAsString(noIdSubtask))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void deleteSubtask() throws Exception {
        mockMvc.perform(delete("/api/subtask/" + subId))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void deleteSubtaskDoesNotExist() throws Exception {
        Mockito.doThrow(new IllegalArgumentException())
                .when(subtaskService).deleteById(subId);
        mockMvc.perform(delete("/api/subtask/" + subId))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}