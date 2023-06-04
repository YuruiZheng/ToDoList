package server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Tag;
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
import server.service.TagService;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class TagControllerTest {

    private static final Tag tag = new Tag(1, 2, "myTagTitle", "000000");
    private static final Tag noIdTag = new Tag(null, 2, "myTagTitle", "000000");
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private static TagService tagService;

    @Autowired
    private WebApplicationContext webApplicationContext;
    private static MockMvc mockMvc;

    @BeforeEach
    private void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void createTag() throws Exception {
        Mockito.when(tagService.createTag(tag))
                .thenReturn(tag);
        mockMvc.perform(post("/api/tag")
                        .content(objectMapper.writeValueAsString(noIdTag))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void getAll() throws Exception {
        Mockito.when(tagService.getAll())
                .thenReturn(List.of(tag));
        MvcResult response = mockMvc.perform(get("/api/tag")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(objectMapper.writeValueAsString(List.of(tag)),
                response.getResponse().getContentAsString());
    }

    @Test
    void getByBoard() throws Exception {
        Mockito.when(tagService.getByBoard(any()))
                .thenReturn(List.of(tag));
        MvcResult response = mockMvc.perform(get("/api/tag/board/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(objectMapper.writeValueAsString(List.of(tag)),
                response.getResponse().getContentAsString());
    }

    @Test
    void updateTag() throws Exception {
        Mockito.when(tagService.updateTag(tag))
                .thenReturn(tag);
        mockMvc.perform(put("/api/tag")
                        .content(objectMapper.writeValueAsString(noIdTag))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    void deleteById() throws Exception {
        Mockito.doNothing().when(tagService).deleteById(tag.getId());
        mockMvc.perform(delete("/api/tag/" + tag.getId()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void deleteByIdDoesNotExist() throws Exception {
        Mockito.doThrow(new IllegalArgumentException()).when(tagService).deleteById(tag.getId());
        mockMvc.perform(delete("/api/tag/" + tag.getId()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteByIdReturnsRandomException() throws Exception {
        Mockito.doThrow(new EntityNotFoundException()).when(tagService).deleteById(tag.getId());
        mockMvc.perform(delete("/api/tag/" + tag.getId()))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }
}