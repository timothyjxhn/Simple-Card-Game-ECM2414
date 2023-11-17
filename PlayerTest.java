import org.junit.*;
import static org.junit.Assert.*;

public class PlayerTest {

    Player player;
    Card[] cardsWin1 = new Card[] {new Card(1), new Card(1), new Card(1), new Card(1)};
    Card[] cardsWin2 = new Card[] {new Card(2), new Card(2), new Card(2), new Card(2)};
    Card[] cardsLose3 = new Card[] {new Card(2), new Card(3), new Card(3), new Card(4)};
    Card[] cardsLose4 = new Card[] {new Card(3), new Card(1), new Card(1), new Card(1)};
    Card[] cardsLose5 = new Card[] {new Card(3), new Card(3), new Card(3), new Card(1)};

    @Before
    public void setUp() { }

    @Test
    public void testSwapCardKeepsPreferred() {
        Card card;

        player = new Player(1, null, null, cardsLose3);
        card = player.swapCard(new Card(1));
        assertNotEquals(3, card.value());

        player = new Player(1, null, null, cardsLose4);
        card = player.swapCard(new Card(1));
        assertNotEquals(1, card.value());

        player = new Player(1, null, null, cardsLose5);
        card = player.swapCard(new Card(1));
        assertNotEquals(3, card.value());
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
        Player player2 = new Player(2, null, null, cardsLose5);
        assertFalse(player1.isWinningHand());
        assertFalse(player2.isWinningHand());
    }
}