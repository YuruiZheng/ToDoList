package server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Board;
import commons.Card;
import commons.ToDoList;
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
import server.service.ToDoListService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class ToDoListControllerTest {

    private static final Board board = new Board(2, "boardName", null, "000000","ffffff", "000000","ffffff", null);
    private static final Card card = new Card(0,1,"title","description","000000", null);
    private static final ToDoList list = new ToDoList(1,board.getId(),"list", List.of(card));
    private static final ToDoList updatedList = new ToDoList(1,board.getId(),"newTitle", List.of(card));
    private static final ToDoList noIdList = new ToDoList(null,board.getId(),"noIdList", List.of(card));
    private static final ToDoList noBoardList = new ToDoList(3,null,"noBoardList", List.of(card));
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private static ToDoListService toDoListService;
    @Autowired
    private WebApplicationContext webApplicationContext;
    private static MockMvc mockMvc;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void getAllLists() throws Exception {
        Mockito.when(toDoListService.getAllLists()).thenReturn(java.util.List.of(list));
        MvcResult response = mockMvc.perform(get("/api/List"))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();

        assertEquals(objectMapper.writeValueAsString(java.util.List.of(list)),
                response.getResponse().getContentAsString());
    }

    @Test
    void createList() throws Exception{
        Mockito.when(toDoListService.createList(list)).thenReturn(list);
        mockMvc.perform(post("/api/List")
                .content(objectMapper.writeValueAsString(list))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void updateList() throws Exception{
        Mockito.when(toDoListService.updateList(list)).thenReturn(updatedList);
        mockMvc.perform(put("/api/List")
                .content(objectMapper.writeValueAsString(updatedList))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void updateListDoesNotExist() throws Exception{
        Mockito.doThrow(new IllegalArgumentException()).when(toDoListService).updateList(list);
        mockMvc.perform(put("/api/List")
                .content(objectMapper.writeValueAsString(list))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void getListById() throws Exception{
        Mockito.when(toDoListService.getListById(noBoardList.getId())).thenReturn(noBoardList);
        MvcResult response = mockMvc.perform(get("/api/List/" + noBoardList.getId()))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(objectMapper.writeValueAsString(noBoardList),
                response.getResponse().getContentAsString());
    }

    @Test
    void getListByIdDoesNotExist() throws Exception{
        Mockito.doThrow(new IllegalArgumentException()).when(toDoListService).getListById(noBoardList.getId());
        mockMvc.perform(get("/api/List/"+noBoardList.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteListById() throws Exception{
        mockMvc.perform(delete("/api/List/" + list.getId()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void deleteListByIdDoesNotExist() throws Exception {
        Mockito.doThrow(new IllegalArgumentException()).when(toDoListService).deleteListById(list.getId());
        mockMvc.perform(delete("/api/List/" + list.getId()))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllListsOnBoard() throws Exception{
        Mockito.when(toDoListService.getAllListsOnBoard(board.getId())).thenReturn(java.util.List.of(list,noIdList));

        MvcResult response =  mockMvc.perform(get("/api/List/board/" + board.getId()))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertEquals(objectMapper.writeValueAsString(java.util.List.of(list,noIdList)),
                response.getResponse().getContentAsString());
    }
}
