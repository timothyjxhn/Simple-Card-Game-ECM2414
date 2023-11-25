import org.junit.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;

public class CardDeckTest {

    CardDeck cardDeck;

    @Before
    public void setUpEach() {
        cardDeck = new CardDeck(1, new Card[] {new Card(1), new Card(2), new Card(3), new Card(4)});
    }

    @AfterClass
    public static void tearDown() {
        CardGameTestSuite.cleanup();
    }

    @Test
    public void testDeckWriteToFile() {
        cardDeck.endDeck();
        try (FileReader fileReader = new FileReader("deck1_output.txt")) {
            char[] contents = new char[100];
            fileReader.read(contents);
            assertEquals("deck1 contents: [1, 2, 3, 4]", new String(contents).trim());
        }
        catch (Exception e) {
            fail("Exception thrown: " + e);
        }
    }

    @Test
    public void pushCard() {
        try {
            cardDeck.pushCard(new Card(5));
            assertEquals(new Card(5), cardDeck.getDeck()[4]);
            assertEquals(5, cardDeck.getDeck().length);
        } catch (InterruptedException e) {
            fail("Exception thrown: " + e);
        }
    }

    @Test
    public void popCard() {
        try {
            Card card = cardDeck.popCard();
            assertEquals(new Card(1), card);
            assertEquals(3, cardDeck.getDeck().length);
        }
        catch (InterruptedException e) {
            fail("Exception thrown: " + e);
        }
    }

    @Test
    public void getDeckName() {
        assertEquals("deck1", cardDeck.getDeckName());
    }
}