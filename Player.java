import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

public class Player {
    private final ArrayBlockingQueue<Card> hand;
    private final int capacity = 4;
    private final int preferredValue;

    public Player(int preferredValue) {
        hand = new ArrayBlockingQueue<>(capacity);
        this.preferredValue = preferredValue;
    }

    public Player(int preferredValue, Card[] cards) {
        hand = new ArrayBlockingQueue<>(capacity);
        this.preferredValue = preferredValue;
        Collections.addAll(hand, cards);
    }

    // Returns the number of cards in the hand.
    public int getHandSize() {
        return hand.size();
    }

    public int getPreferredValue() {
        return preferredValue;
    }

    // Adds a card to the hand.
    // Throws an IllegalStateException if the hand is full.
    public void pushCard(Card card) {
        hand.add(card);
    }

    // Looks at the first card in the hand without removing it.
    // Returns null if the hand is empty.
    public Card peekFirstCard() {
        return hand.peek();
    }

    // Retrieves the first card in the hand and removes it from the hand.
    // Returns null if the hand is empty.
    public Card popCard() {
        return hand.poll();
    }

    public boolean isWinningHand() {
        Object[] handArray = hand.toArray();
        for (int i=0; i<hand.size()-1; i++) {
            Card card = (Card) handArray[i];
            Card nextCard = (Card) handArray[i+1];
            if (!card.equals(nextCard)) {
                return false;
            }
        }
        return true;
    }
}
