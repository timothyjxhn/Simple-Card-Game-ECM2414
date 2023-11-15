import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class CardDeck {
    private final ArrayList<Card> deck;

    public CardDeck(ArrayList<Card> startingDeck) {
        deck = startingDeck;
    }

    public ArrayList<Card> getDeck() {
        return deck;
    }

    /**
     * Adds a card to the deck. Thread safe.
     * @param card The card to be added
     */
    public void pushCard(Card card) {
        synchronized (deck) {
            deck.add(card);
            deck.notify();
        }
    }

    /**
     * Removes a random card. Thread safe, will wait if deck is empty.
     * @return Card
     */
    public Card popCard() {
        Card card;
        synchronized (deck) {
            while (deck.isEmpty()) {
                try {
                    deck.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            int rand = ThreadLocalRandom.current().nextInt(deck.size());

            card = deck.get(rand);
            deck.remove(rand);
        }

        return card;
    }
}
