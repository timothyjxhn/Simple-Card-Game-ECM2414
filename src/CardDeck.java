import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.FileWriter;
import java.io.IOException;

public class CardDeck {

    private final LinkedBlockingQueue<Card> deck;
    private final String deckName;

    /**
     * Constructor for deckNumber. 
     * Note: you do not NEED to provide startingDeck.
     * @param deckNumber the number to be used as deckName.
     */
    public CardDeck(int deckNumber) {
        deck = new LinkedBlockingQueue<>();
        
        if (deckNumber > 0) {
            deckName = "deck" + deckNumber;
            // System.out.println(deckName + " initial contents: " + Arrays.toString(deck.toArray()));
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
        deck = new LinkedBlockingQueue<>();
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
        System.out.println(deckName + " contents: " + Arrays.toString(deck.toArray()));
        try (FileWriter fileWriter = new FileWriter(String.format("%s_output.txt", deckName))) {
            fileWriter.write(String.format("%s contents: %s%n", deckName, deck));
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
    }

    public String getDeckName() {
        return deckName;
    }
}
