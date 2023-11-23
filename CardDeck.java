import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.*;
// This will auto wait for new elements to be added

public class CardDeck {

    private final LinkedBlockingQueue<Card> deck = new LinkedBlockingQueue<>();
    private final String deckName;

    /**
     * Constructor for deckNumber. 
     * Note: you will do not NEED to provide startingDeck.
     * @param deckNumber the number to be used as deckName.
     */
    public CardDeck(int deckNumber) {
        if (deckNumber > 0) {
            deckName = "deck" + deckNumber;
            System.out.println(deckName + " initial contents: " + Arrays.toString(deck.toArray()));
        } else {
            throw new IllegalArgumentException("deckNumber must be >=1");
        }
    }

    /**
     * Constructor for CardDeck
     * @param deckNumber the number to be used in deckName.
     * @param startingDeck Array of Card[] for use as the starting deck.
     */
    public CardDeck(int deckNumber, Card[] startingDeck) {
        deck.addAll(List.of(startingDeck));

        if (deckNumber > 0) {
            deckName = "deck" + deckNumber;
            System.out.println(deckName + " initial contents: " + Arrays.toString(deck.toArray()));
        } else {
            throw new IllegalArgumentException("deckNumber must be >=1");
        }
    }

    /**
     * Method to save the deck state and output its contents to System.out
     * Should be run on game end.
     */
    public void endDeck() {
        printStateToFile();
        System.out.println(deckName + " contents: " + Arrays.toString(deck.toArray()));
    }

    private void printStateToFile() {
        try {
            FileWriter fileWriter = new FileWriter(String.format("%s_output.txt", deckName));
            fileWriter.write(String.format("%s contents: %s%n", deckName, deck));
            fileWriter.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return Card[], the current contents of the deck.
     */
    public Object[] getDeck() {
        return deck.toArray();
    }

    /**
     * Adds a card to the deck. Thread safe.
     * @param card The card to be added
     */
    public void pushCard(Card card) throws InterruptedException {
        deck.put(card);
    }

    /**
     * Removes and returns a card. Thread safe, will wait if deck is empty.
     * @return Card
     */
    public Card popCard() throws InterruptedException {
        return deck.take();
        // return null;
    }

    public String getDeckName() {
        return deckName;
    }
}
