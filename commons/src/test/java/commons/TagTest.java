package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


public class TagTest {

    @Test
    public void applyUpdatesTest(){
        Tag tag1 = new Tag(0,0,"title","#343434");
        Tag tag2 = new Tag(0,1,"updated title","#777777");

        assertNotEquals(tag1,tag2);
        tag1.applyUpdates(tag2);
        assertEquals(tag1,tag2);
    }
}
