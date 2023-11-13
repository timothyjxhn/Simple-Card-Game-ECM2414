import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PlayerTest {

    @Test
    public void testGetHandSize() {
        Card[] cards = {new Card(1), new Card(2), new Card(3)};
        Player player = new Player(1, cards);
        assertEquals(3, player.getHandSize());
    }

    @Test
    public void pushCard() {
        Card[] card = {new Card(1)};
        Player player = new Player(1, card);
        player.pushCard(new Card(2));
        assertEquals(2, player.getHandSize());
    }

    @Test
    public void pushCardWithFullHand() {
        Card[] cards = {new Card(1), new Card(2), new Card(3), new Card(4)};
        Player player = new Player(1, cards);
        try {
            player.pushCard(new Card(5));
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            assertTrue(true);
        }
    }

    @Test
    public void peekFirstCard() {
    }

    @Test
    public void popCard() {
    }

    @Test
    public void testIsWinningHand() {
        Card[] cards = {new Card(1), new Card(1), new Card(1), new Card(1)};
        Card[] cards2 = {new Card(2), new Card(2), new Card(2), new Card(2)};
        Player player = new Player(1, cards);
        Player player2 = new Player(2, cards2);
        assertTrue(player.isWinningHand());
        assertTrue(player2.isWinningHand());
    }

    @Test
    public void testIsNotWinningHand() {
        Card[] cards = {new Card(1), new Card(2), new Card(1), new Card(2)};
        Card[] cards3 = {new Card(3), new Card(3), new Card(3), new Card(4)};
        Player player1 = new Player(1, cards);
        Player player3 = new Player(3, cards3);
        assertFalse(player1.isWinningHand());
        assertFalse(player3.isWinningHand());
    }
}