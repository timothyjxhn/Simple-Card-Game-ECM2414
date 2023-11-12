import org.junit.Test;

import static org.junit.Assert.*;

public class CardTest {


    @Test
    public void testGetValue() {
        assertEquals(1, new Card(1).getValue());
    }

    @Test
    public void testCreateNegative() {
        try {
            new Card(-1);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }
}