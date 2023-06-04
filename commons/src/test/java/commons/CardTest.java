package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CardTest {

    @Test
    void updateBasicCard() {
        Card card1 = new Card(1, 1, "title", "desc", "000000", 1);
        Card card2 = new Card (1, 2, "titl", "dsc", "ffffff", 2);

        assertNotEquals(card1, card2);
        card1.update(card2);
        assertEquals(card1, card2);
    }
}