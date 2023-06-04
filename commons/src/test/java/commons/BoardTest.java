package commons;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class BoardTest {

    @Test
    public void updateColors(){
        Board board1 = new Board(0,"name","password","#343434","font","listBG",
                "listF",1, 2l, null, null, null);

        Board board2 = new Board(0, "name", "password", "#676767","updated font",
                "updated listBG","updated listF", 2,2l,
                null, null, null);

        assertNotEquals(board1,board2);
        board1.updateColors(board2);
        assertEquals(board1,board2);
    }

    @Test
    public void applyUpdatesTest(){
        Board board1 = new Board(0,"name","password","#343434","font","listBG",
                "listF",1, 2l, null, null, null);

        ToDoList list1 = new ToDoList(0,0,"title");
        Tag tag1 = new Tag(0,0,"title", "#454545");
        ColorPreset preset1 = new ColorPreset(0,0,"name","#000000","font");
        Board board2 = new Board(0, "updated name", "updated password", "#676767", "updated font", "updated listBG",
                "updated listF", 2,4l, null,null,null);

        assertNotEquals(board1,board2);
        board1.applyUpdates(board2);
        board2.setLastUpdate(board2.getLastUpdate() +1);
        assertEquals(board1,board2);
    }
}
