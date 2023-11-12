import org.junit.Test;
import static org.junit.Assert.*;

public class CardTest {

    @Test
    public void testGetValue() {
        assertEquals(1, new Card(1).value());
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

    @Test
    public void testCreateZero() {
        try {
            new Card(0);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testCompareCardValues() {
        assertEquals(new Card(1), new Card(1));
        assertNotEquals(new Card(1), new Card(2));
    }
}