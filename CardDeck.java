import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.*;
// This will auto wait for new elements to be added

public class CardDeck {

    private final LinkedBlockingQueue<Card> deck = new LinkedBlockingQueue<>();
    private final String deckName;

    public CardDeck(int deckNumber) {
        if (deckNumber > 0) {
            deckName = "deck" + deckNumber;
        } else {
            throw new IllegalArgumentException("deckNumber must be >=1");
        }
    }

    public CardDeck(int deckNumber, Card[] startingDeck) {
        deck.addAll(List.of(startingDeck));

        if (deckNumber > 0) {
            deckName = "deck" + deckNumber;
        } else {
            throw new IllegalArgumentException("deckNumber must be >=1");
        }
    }

    public void endDeck() {
        printStateToFile();
        System.out.println(deckName + " contents: " + deck.toArray().toString());
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

    public Object[] getDeck() {
        return deck.toArray();
    }

    /**
     * Adds a card to the deck. Thread safe.
     * @param card The card to be added
     */
    public void pushCard(Card card) {
        deck.add(card);
    }

    /**
     * Removes and returns a card. Thread safe, will wait if deck is empty.
     * @return Card
     */
    public Card popCard() {
        try {
            return deck.take();
        } catch (InterruptedException ignored) { }
        return null;
    }

    public String getDeckName() {
        return deckName;
    }
}
