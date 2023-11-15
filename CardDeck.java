import java.util.concurrent.LinkedBlockingQueue;
// This will auto wait for new elements to be added

public class CardDeck {
    // private final LinkedList<Card> deck = new LinkedList<Card>();
    private final LinkedBlockingQueue<Card> deck = new LinkedBlockingQueue<Card>();

    public CardDeck(Card[] startingDeck) {
        deck.addAll(deck);
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
        // Tells the other player waiting on deck to have elements that it can start
    }

    /**
     * Removes and returns a card. Thread safe, will wait if deck is empty.
     * @return Card
     */
    public Card popCard() {
        return deck.poll();
    }
}
