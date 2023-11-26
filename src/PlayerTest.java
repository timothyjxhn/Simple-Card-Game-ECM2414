import org.junit.*;
import static org.junit.Assert.*;
import java.io.File;
import java.lang.reflect.Method;
import java.io.FileReader;
import java.util.LinkedHashMap;

public class PlayerTest {

    static Player player;
    static Card[] cardsWin1;
    static Card[] cardsWin2;
    static Card[] cardsLose3;
    static Card[] cardsLose4;
    static Card[] cardsLose5;

    @Before
    public void setUpEach() {
        cardsWin1 = new Card[] {new Card(1), new Card(1), new Card(1), new Card(1)};
        cardsWin2 = new Card[] {new Card(2), new Card(2), new Card(2), new Card(2)};
        cardsLose3 = new Card[] {new Card(2), new Card(3), new Card(3), new Card(4)};
        cardsLose4 = new Card[] {new Card(3), new Card(1), new Card(1), new Card(1)};
        cardsLose5 = new Card[] {new Card(3), new Card(3), new Card(3), new Card(1)};
    }

    // Close the file writer after each test to avoid file locking
    @After
    public void tearDownEach() {
        if (player != null) player.closeWriter();
    }

    // Delete all output files after all tests have run
    @AfterClass
    public static void tearDown() {
        CardGameTestSuite.cleanup();
    }

    @Test
    public void testCorrectName() {
        player = new Player(151, null, null, cardsWin1);
        assertEquals("player151", player.getName());
    }

    @Test
    public void testOutputFileSetup() {
        player = new Player(369, null, null, cardsWin1);
        File file = new File("player369_output.txt");
        assertTrue(file.exists());
    }

    @Test
    public void testPrintToFile() {
        player = new Player(369, null, null, cardsWin1);
        FileReader fileReader = null;
        try {
            Method printToFile = Player.class.getDeclaredMethod("printToFile", String.class);
            printToFile.setAccessible(true);
            printToFile.invoke(player, "test");
            player.closeWriter();
            fileReader = new FileReader("player369_output.txt");
            char[] contents = new char[100];
            fileReader.read(contents);
            assertTrue(new String(contents).trim().endsWith("test"));
        }
        catch (Exception e) {
            fail("Could not run test due to: " + e);
        }
        finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                }
                catch (Exception ignored) { }
            }
        }
    }

    @Test
    public void testStartWithMoreThanFourCards() {
        try {
            player = new Player(1, null, null, new Card[] {new Card(1), new Card(1), new Card(1), new Card(1),
                    new Card(1), new Card(1), new Card(1), new Card(1)});
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSwapCardKeepsPreferred() {
        player = new Player(1, null, null, cardsLose3);
        Card card = player.swapCard(new Card(1));
        assertTrue(3 != card.value() && 1 != card.value());
    }

    @Test
    public void testSwapCardKeepsPreferred2() {
        player = new Player(1, null, null, cardsLose5);
        Card card = player.swapCard(new Card(1));
        assertNotEquals(3, card.value());
    }

    @Test
    public void testIsWinningHand() {
        player = new Player(1, null, null, cardsWin1);
        assertTrue(player.isWinningHand());
    }

    @Test
    public void testIsWinningHand2() {
        player = new Player(2, null, null, cardsWin2);
        assertTrue(player.isWinningHand());
    }

    @Test
    public void testIsNotWinningHand() {
        player = new Player(1, null, null, cardsLose3);
        assertFalse(player.isWinningHand());
    }

    @Test
    public void testIsNotWinningHand2() {
        player = new Player(2, null, null, cardsLose5);
        assertFalse(player.isWinningHand());
    }

    @Test
    public void testSortedHashMap() {
        try {
            Method sortedHashMap = Player.class.getDeclaredMethod("sortedHashMap", Card[].class);
            sortedHashMap.setAccessible(true);
            LinkedHashMap<Card, Integer> cardMap = (LinkedHashMap<Card, Integer>) sortedHashMap.invoke(null, (Object) cardsLose5);

            assertEquals(3, (int) cardMap.get(new Card(3)));
            assertEquals(1, (int) cardMap.get(new Card(1)));

            Object[] keys = cardMap.keySet().toArray();
            assertEquals(new Card(1), keys[0]);
            assertEquals(new Card(3), keys[1]);
        }
        catch (Exception e) {
            fail("Could not run test due to: " + e);
        }
    }

    @Test
    public void testChangePrefValue() {
        player = new Player(1, null, null, cardsLose5);
        assertEquals(1, player.getPreferredValue());
        try {
            Method sortedHashMap = Player.class.getDeclaredMethod("sortedHashMap", Card[].class);
            sortedHashMap.setAccessible(true);
            LinkedHashMap<Card, Integer> cardMap = (LinkedHashMap<Card, Integer>) sortedHashMap.invoke(player, (Object) cardsLose5);
            Method changePrefValue = Player.class.getDeclaredMethod("setPrefValIfNeeded", LinkedHashMap.class, int.class);
            changePrefValue.setAccessible(true);
            changePrefValue.invoke(player, cardMap, 2);

            assertEquals(3, player.getPreferredValue());
        } catch (Exception e) {
            fail("Could not run test due to: " + e);
        }
    }
}