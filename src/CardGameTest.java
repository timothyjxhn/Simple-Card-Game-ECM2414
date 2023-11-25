import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;
import java.util.ArrayList;

public class CardGameTest {

    static String[] testFiles;

    @BeforeClass
    public static void setUp() {
        testFiles = new String[]{"testPack(5)(40).txt", "testPack(string).txt"};
        for (String testFile : testFiles) {
            if (!new File(testFile).exists()) {
                throw new RuntimeException("Test file " + testFile + " does not exist");
            }
        }
    }

    @AfterClass
    public static void tearDown() {
        CardGameTestSuite.cleanup();
    }

    @Test
    public void testGenerateCardPackCorrectSize() {
        try {
            ArrayList<Card> cardPack = CardGame.generateCardPack("test_output.txt", 3);
            FileReader fileReader = new FileReader("test_output.txt");
            int c;
            int i = 0;
            while ((c = fileReader.read()) != -1) {
                if (c == '\n') {
                    i++;
                }
            }
            assertEquals(3*8, cardPack.size());
            assertEquals(i, cardPack.size());
            fileReader.close();
        }
        catch (Exception e) {
            fail("Test failed due to exception: " + e);
        }
    }

    @Test
    public void testCardDeckFromFileCorrectSize() {
        try {
            ArrayList<Card> cardPack = CardGame.cardDeckFromFile(testFiles[0], 5);
            assertEquals(40, cardPack.size());
        }
        catch (Exception e) {
            fail("Test failed due to exception: " + e);
        }
    }

    @Test
    public void testCardDeckFromFileWrongSize() {
        try {
            CardGame.cardDeckFromFile(testFiles[0], 20);
            fail("Test failed due to exception: FileNotDeckException not thrown");
        }
        catch (FileNotDeckException e) {
            assertTrue(true);
        }
        catch (Exception e) {
            fail("Test failed due to exception: " + e);
        }
    }

    @Test
    public void testCardDeckFromFileIncludesStrings() {
        try {
            CardGame.cardDeckFromFile(testFiles[1], 5);
            fail("Test failed due to exception: FileNotDeckException not thrown");
        }
        catch (FileNotDeckException e) {
            assertTrue(true);
        }
        catch (Exception e) {
            fail("Test failed due to exception: " + e);
        }
    }

    @Test
    public void testInputWrongFilePath() {
        try {
            CardGame.cardDeckFromFile("wrongPath.txt", 5);
            fail("Test failed due to exception: FileNotFoundException not thrown");
        }
        catch (FileNotFoundException e) {
            assertTrue(true);
        }
        catch (Exception e) {
            fail("Test failed due to exception: " + e);
        }
    }
}