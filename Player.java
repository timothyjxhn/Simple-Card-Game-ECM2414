// import java.util.concurrent.ArrayBlockingQueue;
// ArrayLinkedList does not allow for removing random elements. Therefore can not be used to remove only non-preferred cards.

import java.util.*;

public class Player implements Runnable {
    private final String name;
    private final Card[] hand;
    private final static int capacity = 4;
    private final int preferredValue;
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

                // Decide card to give
                /*
                int cardIndex = - 1; // The index to be swapped out
                for (int i=0; i<hand.length; i++) {
                    if (!hand[i].equals(takenCard)) { // Not a preferred card:
                        cardIndex = i;
                        break;
                    }
                }
                */

                Card cardToGive = swapCard(takenCard);
                giveDeck.pushCard(cardToGive);
                System.out.printf("%s discards a %s to %s%n", name, cardToGive.toString(), giveDeck.getDeckName()); // e.g. player 1 discards 3 3 to deck 2
                System.out.printf("%s current hand is %s%n", name, Arrays.toString(hand)); // eg.g player 1 current hand is [1, 4, 2, 1]
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
        this.hand = new Card[capacity];
        this.preferredValue = preferredValue;

        this.giveDeck = giveDeck;
        this.takeDeck = takeDeck;

        // Player number derived from its preferred value
        if (preferredValue > 0) {
            name = "player" + preferredValue;
        } else {
            throw new IllegalArgumentException("playerNumber must be > 1");
        }
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

    // Swaps card for best chance at winning hand. Will swap least occurring card.
    // Removes and retrieves the card that was swapped out.
    public Card swapCard(Card card) {
        Card[] tempHand = new Card[capacity + 1];
        System.arraycopy(hand, 0, tempHand, 0, hand.length);
        tempHand[capacity] = card;

        LinkedHashMap<Card, Integer> cardMap = sortedHashMap(tempHand);
        Card cardToDrop = cardMap.keySet().iterator().next();

        // TODO: Add case to keep preferred card if there is no majority.

        for (int i=0; i<hand.length; i++) {
            if (hand[i].equals(cardToDrop)) {
                hand[i] = card;
                break;
            }
        }
        return cardToDrop;
    }

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

    public boolean isWinningHand() {
        Object[] handArray = hand;
        for (int i=0; i<hand.length-1; i++) {
            Card card = (Card) handArray[i];
            Card nextCard = (Card) handArray[i+1];
            if (!card.equals(nextCard)) {
                return false;
            }
        }
        return true;
    }
}
