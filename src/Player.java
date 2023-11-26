import java.util.*;
import java.lang.Math;
import java.io.FileWriter;
import java.io.IOException;

public class Player implements Runnable {
    private final String name;
    private final Card[] hand;
    private final static int capacity = 4;
    private int preferredValue;
    private final CardDeck giveDeck;
    private final CardDeck takeDeck;
    private final FileWriter fileWriter;

    /**
     * Constructor for Player.
     * @param preferredValue int for an initial preferred value.
     * @param giveDeck A CardDeck that cards will be given too.
     * @param takeDeck CardDeck that cards will be taken from.
     * @param startingHand An array of Card[] that will be used as the starting hand.
     */
    public Player(int preferredValue, CardDeck giveDeck, CardDeck takeDeck, Card[] startingHand) {
        if (preferredValue < 1) {
            throw new IllegalArgumentException("preferredValue must be > 0");
        }

        this.hand = new Card[capacity];
        this.preferredValue = preferredValue;
        name = "player" + preferredValue; // name is related to initial preferredValue

        this.giveDeck = giveDeck;
        this.takeDeck = takeDeck;

        if (startingHand.length > capacity) {
            throw new IllegalArgumentException("startingHand must be or less than " + capacity);
        }
        System.arraycopy(startingHand, 0, hand, 0, startingHand.length);

        try {
            fileWriter = new FileWriter(String.format("player%d_output.txt", preferredValue));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        printToFile(String.format("%s initial hand %s%n", name, Arrays.toString(hand)));
    }

    @Override
    public void run() {
        System.out.printf("%s has started with a hand of %s %n", name, Arrays.toString(hand));
        while (!Thread.currentThread().isInterrupted()) {
            if (isWinningHand()) {
                String win = String.format("%s has won%n", name);
                printToFile(win);
                System.out.println(win);

                // Chang CardGame win to be true
                if (CardGame.endGame(this)) {
                    win = String.format("%s has been accepted as the winner%n", name);
                    printToFile(win);
                    System.out.print(win);
                } else {
                    win = String.format("%s attempted to win but a winner had already been chosen%n", name);
                    System.out.print(win);
                    printToFile(win);
                }
            }
            else {
                // Take card
                try {
                    Card takenCard = takeDeck.popCard();
                    printToFile(String.format("%s draws a %s from %s%n", name, takenCard.toString(),
                            takeDeck.getDeckName())); // e.g. player 1 draws a 2 from deck 4

                    Card cardToGive = swapCard(takenCard);
                    giveDeck.pushCard(cardToGive);
                    // e.g. player 1 discards 3 3 to deck 2
                    printToFile(String.format("%s discards a %s to %s%n", name, cardToGive.toString(),
                            giveDeck.getDeckName()));
                    // e.g. player 1 current hand is [1, 4, 2, 1]
                    printToFile(String.format("%s current hand is %s%n", name, Arrays.toString(hand)));
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

        printToFile(String.format("%s exits%n", name));
        System.out.printf("%s exits%n", name);
        try {
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return String, the current name
     */
    public String getName() {
        return name;
    }

    /**
     * @return int, the current preferred value
     */
    public int getPreferredValue() {
        return preferredValue;
    }

    /**
     * Print `s` to the file defined in `private final fileWriter`.
     * @param s String to be written.
     */
    private void printToFile(String s) {
        try {
            fileWriter.write(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Closes the FileWriter.
     */
    public void closeWriter() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * Will update preferred card if most occurring card is majority.
     * Order of preference in discarding:
     * 1. Non-preferred card occurring least
     * 2. Preferred card occurring least
     * 3. Non-preferred card occurring most
     */
    /**
     * Swaps card for best chance at winning hand. Will swap least occurring card.
     * 
     * @param card: The card to be swapped in.
     * @return The card that was swapped out.
     */
    public Card swapCard(Card card) {
        Card[] tempHand = new Card[capacity + 1];
        System.arraycopy(hand, 0, tempHand, 0, hand.length);
        tempHand[capacity] = card;

        LinkedHashMap<Card, Integer> cardMap = sortedHashMap(tempHand);
        setPrefValIfNeeded(cardMap, (int) Math.ceil((double) tempHand.length / 2));

        Iterator<Card> iterator = cardMap.keySet().iterator();
        Card cardToDrop;
        do {
            cardToDrop = iterator.next();
        } while (cardToDrop.value() == preferredValue && iterator.hasNext());

        for (int i = 0; i < hand.length; i++) {
            if (hand[i].equals(cardToDrop)) {
                hand[i] = card;
                break;
            }
        }
        return cardToDrop;
    }

    /**
     * Sorts a HashMap by integer value.
     * 
     * @param hand: Array of cards to be weighed and sorted.
     * @return A LinkedHashMap sorted by integer value.
     */
    private static LinkedHashMap<Card, Integer> sortedHashMap(Card[] hand) {
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
     * Sets preferredValue if most occurring card is majority (more than half of
     * hand).
     * 
     * @param cardMap:   A sorted LinkedHashMap of cards and their occurrences.
     * @param tolerance: The minimum number of occurrences for a card to be
     *                   considered majority.
     */
    private void setPrefValIfNeeded(LinkedHashMap<Card, Integer> cardMap, int tolerance) {
        Card mostOccurringCard = new LinkedList<>(cardMap.keySet()).descendingIterator().next();
        if (cardMap.get(mostOccurringCard) >= tolerance && mostOccurringCard.value() != preferredValue) {
            preferredValue = mostOccurringCard.value();
        }
    }

    public boolean isWinningHand() {
        for (int i = 0; i < hand.length - 1; i++) {
            if (!hand[i].equals(hand[i + 1])) {
                return false;
            }
        }
        return true;
    }
}
