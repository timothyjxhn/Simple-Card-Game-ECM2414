// import java.util.concurrent.ArrayBlockingQueue;
// ArrayLinkedList does not allow for removing random elements. Therefore can not be used to remove only non-preferred cards.

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
            } else {
                // Take card
                Card takenCard = takeDeck.popCard();
                System.out.printf("%s draws a %s from %s%n", name, takenCard.toString(), takeDeck.getDeckName()); // e.g. player 1 draws a 2 from deck 4

                // Decide card to give
                updatePreferredValue();
                int cardIndex = - 1; // The index to be swapped out
                for (int i=0; i<hand.length; i++) {
                    if (!hand[i].equals(takenCard)) { // Not a preferred card:
                        cardIndex = i;
                        break;
                    }
                }

                Card cardToGive = hand[cardIndex]; 
                hand[cardIndex] = takenCard;
                giveDeck.pushCard(cardToGive);
                System.out.printf("%s discards a %s to %s%n", name, cardToGive.toString(), giveDeck.getDeckName()); // e.g. player 1 discards 3 3 to deck 2
                System.out.printf("%s current hand is %s%n", name, hand.toString()); // eg.g player 1 current hand is [1, 4, 2, 1]
            }
        }
        // TODO: End game file save
        System.out.printf("%s exits", name);
        throw new UnsupportedOperationException("Unimplemented end");
    }

    public void stopThread() {
        stopRequested = true;
    }

    public Player(int playerNumber, int preferredValue, Card[] cards, CardDeck giveDeck, CardDeck takeDeck) {
        this.hand = new Card[capacity];
        this.preferredValue = preferredValue;

        this.giveDeck = giveDeck;
        this.takeDeck = takeDeck;
        
        if (playerNumber < 1) {
            name = "player " + playerNumber;
        } else {
            throw new IllegalArgumentException("playerNumber must be > 1");
        }

        System.out.printf("%s initial hand %s%n", name, hand.toString());
    }

    // Returns the number of cards in the hand.
    public int getHandSize() {
        return hand.length;
    }

    public int getPreferredValue() {
        return preferredValue;
    }

    public void updatePreferredValue() {
        // TODO
        throw new UnsupportedOperationException("Unimplemented method");
    }

    public String getName() {
        return name;
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
