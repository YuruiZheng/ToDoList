package server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Card;
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
import server.service.CardService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class CardControllerTest {

    private static final Integer cardId=1;
    private static final Card card = new Card(cardId, 1, "title1", "desc1","000000", null);
    private static final Card card1 = new Card(2, 1, "title", "desc1","000000", null);
    private static final Card updatedCard = new Card(cardId, 2, "title1", "desc", "000001", null);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @MockBean
    private static CardService cardService;
    @Autowired
    private WebApplicationContext webApplicationContext;
    private static MockMvc mockMvc;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void getAll() throws Exception {
        Mockito.when(cardService.getAll()).thenReturn(List.of(card));
        MvcResult result = mockMvc.perform(get("/api/card")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(objectMapper.writeValueAsString(List.of(card)),
        result.getResponse().getContentAsString());
    }

    @Test
    void getCardsFromListById() throws Exception{
        Mockito.when(cardService.getCardsFromListById(1))
                .thenReturn(List.of(card,card1));
        MvcResult result = mockMvc.perform(get("/api/card/list/1")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(objectMapper.writeValueAsString(List.of(card,card1)),
                result.getResponse().getContentAsString());
    }

    @Test
    void createCard() throws Exception {
        Mockito.when(cardService.createCard(card))
                .thenReturn(card);
        MvcResult result = mockMvc.perform(post("/api/card")
                .content(objectMapper.writeValueAsString(card))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
        assertEquals(objectMapper.writeValueAsString(card),
                result.getResponse().getContentAsString());
    }

    @Test
    void getById() throws Exception{
        Mockito.when(cardService.getById(cardId))
                .thenReturn(card);
        MvcResult result = mockMvc.perform(get("/api/card/" + cardId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(objectMapper.writeValueAsString(card),
                result.getResponse().getContentAsString());
    }

    @Test
    void getByIdDoesNotExist() throws Exception{
        Mockito.doThrow(new IllegalArgumentException()).when(cardService).getById(cardId);
        mockMvc.perform(get("/api/card/" + cardId))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteCard() throws Exception {
        mockMvc.perform(delete("/api/card/"+cardId))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void deleteCardFail() throws Exception{
        Mockito.doThrow(new IllegalArgumentException())
                .when(cardService).deleteById(cardId);
        mockMvc.perform(delete("/api/card/"+cardId))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteAll() throws Exception {
        mockMvc.perform(delete("/api/card/deleteAll"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void updateCard() throws Exception {
        Mockito.when(cardService.updateCard(updatedCard))
                .thenReturn(updatedCard);
        MvcResult result = mockMvc.perform(put("/api/card/")
                        .content(objectMapper.writeValueAsString(updatedCard))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        assertEquals(objectMapper.writeValueAsString(updatedCard),
                result.getResponse().getContentAsString());
    }
}