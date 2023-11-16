import org.junit.*;
import static org.junit.Assert.*;

public class PlayerTest {

    Card[] cardsWin1;
    Card[] cardsWin2;
    Card[] cardsLose3;
    Card[] cards4NotFull;
    Card[] cards5NotFull;

    @Before
    public void setUp() {
        cardsWin1 = new Card[] {new Card(1), new Card(1), new Card(1), new Card(1)};
        cardsWin2 = new Card[] {new Card(2), new Card(2), new Card(2), new Card(2)};
        cardsLose3 = new Card[] {new Card(2), new Card(3), new Card(5), new Card(4)};
        cards4NotFull = new Card[] {new Card(4), new Card(4), new Card(4)};
        cards5NotFull = new Card[] {new Card(5), new Card(5)};
    }

    @Test
    public void testGetHandSize() {
        Player player = new Player(1, null, null, cards5NotFull);
        assertEquals(2, player.getHandSize());
    }

    @Test
    public void pushCard() {
        Player player = new Player(1, null, null, cards4NotFull);
        Player player2 = new Player(2, null, null);
        player.pushCard(new Card(2));
        player2.pushCard(new Card(4));
        assertEquals(4, player.getHandSize());
        assertEquals(1, player2.getHandSize());
    }

    @Test
    public void pushCardWithFullHand() {
        Player player = new Player(1, null, null, cardsWin1);
        try {
            player.pushCard(new Card(5));
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            assertTrue(true);
        }
    }

    @Test
    public void popCard() {
        Player player = new Player(1, null, null, cardsLose3);
        assertEquals(3, player.popCard(3).value());
        assertEquals(3, player.getHandSize());
    }

    @Test
    public void testIsWinningHand() {
        Player player = new Player(1, null, null, cardsWin1);
        Player player2 = new Player(2, null, null, cardsWin2);
        assertTrue(player.isWinningHand());
        assertTrue(player2.isWinningHand());
    }

    @Test
    public void testIsNotWinningHand() {
        Player player1 = new Player(1, null, null, cardsLose3);
        assertFalse(player1.isWinningHand());
    }
}