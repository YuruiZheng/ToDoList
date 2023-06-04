package server.service.impl;

import commons.Board;
import commons.Card;
import commons.ToDoList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import server.database.ToDoListRepository;
import server.service.ToDoListService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ToDoListServiceImplTest {
    private static final Board board = new Board(1, "boardName", null, "000000","000000", "000000","ffffff", null);
    private static final Card card = new Card(0,1,"title","description","000000", null);
    private static final ToDoList list = new ToDoList(1,board.getId(),"list", List.of(card));
    private static final ToDoList updatedList = new ToDoList(1,board.getId(),"UpdatedTitle", List.of(card));
    private static final ToDoList secondList = new ToDoList(2,board.getId(),"list", List.of(card));

    @Mock
    private static ToDoListRepository toDoListRepository;
    private static ToDoListService toDoListService;

    @BeforeEach
    public void init(){
        toDoListService = new ToDoListServiceImpl(toDoListRepository);
    }

    @Test
    void getAllLists(){
        Mockito.when(toDoListRepository.findAll())
                .thenReturn(java.util.List.of(list));
        assertEquals(java.util.List.of(list), toDoListService.getAllLists());
    }

    @Test
    void createList() {
        Mockito.when(toDoListRepository.save(list))
                .thenReturn(list);
        assertEquals(list, toDoListService.createList(list));
    }

    @Test
    void updateList(){
        Mockito.when(toDoListRepository.findById(list.getId())).thenReturn(Optional.of(updatedList));
        Mockito.when(toDoListRepository.save(list)).thenReturn(list);
        assertEquals(updatedList, toDoListService.updateList(list));
    }

    @Test
    void getListById(){
        Mockito.when(toDoListRepository.findById(list.getId()))
                .thenReturn(Optional.of(list));
        assertEquals(list, toDoListService.getListById(list.getId()));
    }

    @Test
    void deleteListById(){
        assertDoesNotThrow(() -> toDoListService.deleteListById(1));
    }

    @Test
    void getAllListsOnBoard(){
        Mockito.when(toDoListRepository.findAllListByBoardId(board.getId()))
                .thenReturn(List.of(list,secondList));
        assertEquals(List.of(list,secondList), toDoListService.getAllListsOnBoard(board.getId()));
    }

}
