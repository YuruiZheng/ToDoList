package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ColorPresetTest {

    @Test
    public void updateTest(){
        ColorPreset preset1 = new ColorPreset(0,0,"name","#343434","font");
        ColorPreset preset2 = new ColorPreset(0,0,"updated name","#777777","updated");

        assertNotEquals(preset1,preset2);
        preset1.update(preset2);
        assertEquals(preset1,preset2);
    }

    @Test
    public void toStringTest(){
        ColorPreset preset1 = new ColorPreset(0,0,"name","#343434","font");
        assertEquals(preset1.getName(),preset1.toString());
    }
}
