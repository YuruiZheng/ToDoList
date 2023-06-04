package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


public class SubtaskTest {

    @Test
    public void updateTest(){
        Subtask task1 = new Subtask(0,0,"title",false,0);
        Subtask task2 = new Subtask(0,0,"updated title",true, 1);

        assertNotEquals(task1,task2);
        task1.update(task2);
        assertEquals(task1,task2);
    }
}
