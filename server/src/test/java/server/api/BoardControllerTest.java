package server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import server.service.AdminService;
import server.service.BoardService;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BoardControllerTest {

    @Value(value="${local.server.port}")
    private int port;
    private static final Board board = new Board(1, "myBoardName", "myPassword", "000000","ffffff", "000000","ffffff", null);
    private static final Board noIdBoard = new Board(null, "myBoardName", "myPassword", "000000","ffffff", "000000","ffffff", null);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @MockBean
    private static BoardService boardService;

    @MockBean
    private static AdminService adminService;
    @Autowired
    private WebApplicationContext webApplicationContext;
    private static MockMvc mockMvc;
    private WebSocketStompClient stompClient;

    @BeforeEach
    private void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        when(adminService.checkPassword(anyString())).thenReturn(true);
    }

    @Test
    void getAll() throws Exception {
        when(boardService.getAll())
                .thenReturn(List.of(board));
        MvcResult response = mockMvc.perform(get("/api/board")
                        .header(HttpHeaders.AUTHORIZATION, "1234"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(objectMapper.writeValueAsString(List.of(board)),
                response.getResponse().getContentAsString());
    }

    @Test
    void updateBoard() throws Exception {
        when(boardService.updateBoard(board))
                .thenReturn(board);
        mockMvc.perform(put("/api/board")
                        .content(objectMapper.writeValueAsString(noIdBoard))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void createBoard() throws Exception {
        when(boardService.createBoard(board))
                .thenReturn(board);
        mockMvc.perform(post("/api/board")
                        .content(objectMapper.writeValueAsString(noIdBoard))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

    }

    @Test
    void getById() throws Exception {
        when(boardService.getById(board.getId()))
                .thenReturn(board);
        MvcResult response = mockMvc.perform(get("/api/board/" + board.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(objectMapper.writeValueAsString(board),
                response.getResponse().getContentAsString());
    }

    @Test
    void deleteById() throws Exception {
        mockMvc.perform(delete("/api/board/" + board.getId()))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    void deleteByIdDoesNotExist() throws Exception {
        Mockito.doThrow(new IllegalArgumentException()).when(boardService).deleteById(board.getId());
        mockMvc.perform(delete("/api/board/" + board.getId()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    @Test
    void deleteByIdThrowsRandomException() throws Exception {
        Mockito.doThrow(new EntityNotFoundException()).when(boardService).deleteById(board.getId());
        mockMvc.perform(delete("/api/board/" + board.getId()))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    void longPollNewBoardTest() throws Exception {
        when(boardService.waitForChanges()).thenReturn(List.of(board));
        MvcResult result = mockMvc.perform(get("/api/board/long-poll-changes")
                        .header(HttpHeaders.AUTHORIZATION, "1234"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(objectMapper.writeValueAsString(List.of(board)),result.getResponse().getContentAsString());
    }

    @Test
    void longPollNewBoardException() throws Exception {
        when(boardService.waitForChanges()).thenThrow(new InterruptedException());
        mockMvc.perform(get("/api/board/long-poll-changes")
                        .header(HttpHeaders.AUTHORIZATION, "1234"))
                .andDo(print()).andExpect(status().isNoContent());
    }

    @Test
    void longPollBoardUpdateTest() throws Exception {
        when(boardService.waitForBoardUpdate(board.getId())).thenReturn(board);
        MvcResult result = mockMvc.perform(get("/api/board/long-poll/" + board.getId()))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(objectMapper.writeValueAsString(board),result.getResponse().getContentAsString());
    }

    @Test
    void longPollBoardUpdateException() throws Exception {
        when(boardService.waitForBoardUpdate(board.getId())).thenThrow(new InterruptedException());
        mockMvc.perform(get("/api/board/long-poll/" + board.getId()))
                .andDo(print()).andExpect(status().isNoContent());
    }
}