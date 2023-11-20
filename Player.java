// import java.util.concurrent.ArrayBlockingQueue;
// ArrayLinkedList does not allow for removing random elements. Therefore can not be used to remove only non-preferred cards.

import java.util.*;
import java.lang.Math;

public class Player implements Runnable {
    private final String name;
    private final Card[] hand;
    private final static int capacity = 4;
    private int preferredValue;
    private final CardDeck giveDeck;
    private final CardDeck takeDeck;
    private volatile Boolean stopRequested = false; // This could be changed to static if game end is controlled by thread?

    @Override
    public void run() {
        while (!stopRequested) {
            if (isWinningHand()) {
                // TODO: Win
                System.out.printf("%s wins%n", name);

                // Chang CardGame win to be true
                System.out.printf("%s has informed other players that %s has won%n", name, name);
                throw new UnsupportedOperationException("Unimplemented win");
            }
            else {
                // Take card
                Card takenCard = takeDeck.popCard();
                System.out.printf("%s draws a %s from %s%n", name, takenCard.toString(), takeDeck.getDeckName()); // e.g. player 1 draws a 2 from deck 4

                Card cardToGive = swapCard(takenCard);

                giveDeck.pushCard(cardToGive);
                System.out.printf("%s discards a %s to %s%n", name, cardToGive.toString(), giveDeck.getDeckName()); // e.g. player 1 discards 3 3 to deck 2
                System.out.printf("%s current hand is %s%n", name, Arrays.toString(hand)); // e.g. player 1 current hand is [1, 4, 2, 1]
            }
        }
        // TODO: End game file save
        System.out.printf("%s exits", name);
        throw new UnsupportedOperationException("Unimplemented end");
    }

    public void stopThread() {
        stopRequested = true;
    }

    public Player(int preferredValue, CardDeck giveDeck, CardDeck takeDeck) {
        if (preferredValue < 1) {
            throw new IllegalArgumentException("preferredValue must be > 0");
        }
        this.hand = new Card[capacity];
        this.preferredValue = preferredValue;
        name = "player" + preferredValue;

        this.giveDeck = giveDeck;
        this.takeDeck = takeDeck;
    }

    public Player(int preferredValue, CardDeck giveDeck, CardDeck takeDeck, Card[] startingHand) {
        this(preferredValue, giveDeck, takeDeck);
        if (startingHand.length > capacity) {
            throw new IllegalArgumentException("startingHand must be or less than " + capacity);
        }
        System.arraycopy(startingHand, 0, hand, 0, startingHand.length);
        System.out.printf("%s initial hand %s\n", name, Arrays.toString(hand));
    }

    public int getPreferredValue() {
        return preferredValue;
    }

    public String getName() {
        return name;
    }

    public void printHand() {
        System.out.println(Arrays.toString(hand));
    }

    /*
    Will update preferred card if most occurring card is majority.
    Order of preference in discarding:
    1. Non-preferred card occurring least
    2. Preferred card occurring least
    3. Non-preferred card occurring most
     */
    /**
     * Swaps card for best chance at winning hand. Will swap least occurring card.
     * @param card: The card to be swapped in.
     * @return The card that was swapped out.
     */
    public Card swapCard(Card card) {
        Card[] tempHand = new Card[capacity + 1];
        System.arraycopy(hand, 0, tempHand, 0, hand.length);
        tempHand[capacity] = card;

        LinkedHashMap<Card, Integer> cardMap = sortedHashMap(tempHand);
        setPrefValIfNeeded(cardMap, (int) Math.ceil((double)tempHand.length/2));

        Iterator<Card> iterator = cardMap.keySet().iterator();
        Card cardToDrop;
        do {
            cardToDrop = iterator.next();
        } while (cardToDrop.value() == preferredValue && iterator.hasNext());

        for (int i=0; i<hand.length; i++) {
            if (hand[i].equals(cardToDrop)) {
                hand[i] = card;
                break;
            }
        }
        return cardToDrop;
    }

    /**
     * Sorts a HashMap by integer value.
     * @param hand: Array of cards to be weighed and sorted.
     * @return A LinkedHashMap sorted by integer value.
     */
    private LinkedHashMap<Card, Integer> sortedHashMap(Card[] hand) {
        HashMap<Card, Integer> cardMap = new HashMap<>(hand.length);
        for (Card handCard : hand) {
            if (cardMap.containsKey(handCard)) {
                cardMap.put(handCard, cardMap.get(handCard) + 1);
            } else {
                cardMap.put(handCard, 1);
            }
        }

        LinkedHashMap<Card, Integer> sortedMap = new LinkedHashMap<>();
        ArrayList<Integer> list = new ArrayList<>(cardMap.values());
        list.sort(null);
        for (int num : list) {
            for (Map.Entry<Card, Integer> entry : cardMap.entrySet()) {
                if (entry.getValue().equals(num)) {
                    sortedMap.put(entry.getKey(), num);
                }
            }
        }

        return sortedMap;
    }

    /**
     * Sets preferredValue if most occurring card is majority (more than half of hand).
     * @param cardMap: A sorted LinkedHashMap of cards and their occurrences.
     * @param tolerance: The minimum number of occurrences for a card to be considered majority.
     */
    private void setPrefValIfNeeded(LinkedHashMap<Card, Integer> cardMap, int tolerance) {
        Card mostOccurringCard = new LinkedList<>(cardMap.keySet()).descendingIterator().next();
        if (cardMap.get(mostOccurringCard) >= tolerance && mostOccurringCard.value() != preferredValue) {
            preferredValue = mostOccurringCard.value();
        }
    }

    public boolean isWinningHand() {
        for (int i=0; i<hand.length-1; i++) {;
            if (!hand[i].equals(hand[i+1])) {
                return false;
            }
        }
        return true;
    }
}
