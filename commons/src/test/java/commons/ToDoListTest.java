package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ToDoListTest {

    private ToDoList list1;
    private ToDoList list2;
    private ToDoList listOtherId;
    private Board board;

    @BeforeEach
    public void init() {
        board = new Board(1, "board", null, "ffffff", "ffffff", "ffffff",
                "ffffff", null);

        list1 = new ToDoList(0, board.getId(), "title");
        list2 = new ToDoList(0, board.getId(), "title");
        listOtherId = new ToDoList(1, board.getId(), "title");
    }

    @Test
    public void constructorTest() {
        assertEquals(0, list1.getId());
        assertEquals(board.getId(), list1.getBoardId());
        assertEquals("title", list1.getTitle());
    }

    @Test
    public void equalsTest() {
        assertEquals(list1, list2);

        ToDoList list3 = new ToDoList(3, board.getId(), "list");
        ToDoList list4 = new ToDoList(3, board.getId(), "list");

        assertEquals(list3, list4);
    }

    @Test
    public void notEqualsTest() {
        ToDoList list3 = new ToDoList(3, board.getId(), "list");

        assertNotEquals(list1, list3);
        assertNotEquals(list1, listOtherId);
    }

    @Test
    public void hashMethodEquals() {
        assertEquals(list1, list2);
        assertEquals(list1.hashCode(), list2.hashCode());

        ToDoList list3 = new ToDoList(3, board.getId(), "list");
        ToDoList list4 = new ToDoList(3, board.getId(), "list");

        assertEquals(list3, list4);
        assertEquals(list3.hashCode(), list4.hashCode());
    }

    @Test
    public void hashMethodNotEquals() {
        ToDoList list3 = new ToDoList(3, board.getId(), "list");

        assertNotEquals(list1, listOtherId);
        assertNotEquals(list1.hashCode(), listOtherId.hashCode());

        assertNotEquals(list1, list3);
        assertNotEquals(list1.hashCode(), list3.hashCode());
    }

    @Test
    public void UpdateListTest() {
        ToDoList list = new ToDoList(1, board.getId(), "title");
        ToDoList updatedList = new ToDoList(1, board.getId(), "updatedTitle");

        assertNotEquals(list, updatedList);

        list.updateList(updatedList);
        assertEquals(list, updatedList);
    }
}
