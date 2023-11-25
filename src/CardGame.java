import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class CardGame {
    private final static AtomicBoolean winnerSelected = new AtomicBoolean(false);
    private final static ArrayList<Player> players = new ArrayList<>();
    private final static ArrayList<CardDeck> decks = new ArrayList<>();
    private final static ArrayList<Thread> playerThreads = new ArrayList<>();
    private static int playerCount;

    public static void main(String[] args) {
        ArrayList<Card> cardPack;

        // Getting user input
        try (Scanner scanner = new Scanner(System.in)) {
            do {
                System.out.print("Enter the number of players: ");
                try {
                    playerCount = scanner.nextInt();
                    if (playerCount < 2) {
                        throw new IllegalArgumentException();
                    }
                }
                catch (IllegalArgumentException e) {
                    System.out.println("\nInvalid input. There must be at least 2 players");
                }
                catch (Exception e) {
                    System.out.println("\nInvalid input.");
                }
                finally {
                    scanner.nextLine();
                }
            } while (playerCount < 2);

            // Get card deck
            while (true) {
                System.out.print("\nEnter the card deck path (leave input blank to generate):\n");
                String input = scanner.nextLine();

                if (input.isEmpty()) {
                    System.out.println("The card deck will be generated");

                    cardPack = generateCardPack(playerCount);
                    break;
                }

                try {
                    cardPack = cardDeckFromFile(input, playerCount);
                    break;
                } catch (FileNotFoundException e) {
                    System.out.println("\nInvalid input. The file could not be found:\n" + e);
                } catch (FileNotDeckException e) {
                    System.out.println("\nInvalid input. The file was an invalid deck:\n" + e);
                } catch (IOException e) {
                    System.out.println("\nInvalid input.\n" + e);
                }
            }
        }

        Collections.shuffle(cardPack);

        // Generate decks
        for (int deck_i = 1; deck_i <= playerCount; deck_i++) {
            decks.add(new CardDeck(deck_i));
        }

        // Generate players and distribute cards
        for (int player_i = 1; player_i <= playerCount; player_i++) {
            Card[] hand = new Card[] {cardPack.remove(0), cardPack.remove(0), cardPack.remove(0), cardPack.remove(0)};
            players.add(new Player(player_i, decks.get((player_i) % playerCount), decks.get(player_i - 1), hand));
            playerThreads.add(new Thread(players.getLast(), players.getLast().getName()));
        }

        // Distribute remaining cards to decks
        int index = 0;
        while (!cardPack.isEmpty()) {
            try {
                decks.get(index).pushCard(cardPack.remove(0));
            }
            catch (InterruptedException ignored) { }
            finally {
                index = (index + 1) % playerCount;
            }
        }


        // This could be inserted above but the distance in start times could 
        // lead to some decks being significantly larger.
        for (Thread player : playerThreads) {
            player.start();
        }
    }

    /**
     * Generates a deck for playerCount. Generated deck is put in ./generated-deck.txt
     * @param playerCount
     * @return An ArrayList of Cards.
     */
    private static ArrayList<Card> generateCardPack(int playerCount) {
        return generateCardPack("./generated-deck.txt", playerCount);
    }

    /**
     * Generates a deck for provided playerCount, saves it in generatedFilePath.
     * @param generatedFilePath
     * @param playerCount
     * @return An ArrayList of Cards, the deck.
     */
    protected static ArrayList<Card> generateCardPack(String generatedFilePath, int playerCount) {
        try (FileWriter fileWriter = new FileWriter(generatedFilePath)) {
            ArrayList<Card> deck = new ArrayList<>();
            for (int i = 0; i<(8 * playerCount); i++) {
                Card card = new Card(new Random().nextInt(playerCount) + 1);
                deck.add(card);
                fileWriter.write(card + "\n");
            }
            return deck;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Looks for a valid deck file at `path` for provided `playerCount`. 
     * Will error if invalid or not present.
     * @param path
     * @param playerCount
     * @return ArrayList of Cards, the deck.
     * @throws FileNotFoundException
     * @throws FileNotDeckException The provided file was not a valid deck (for provided playerCount).
     * @throws IOException
     */
    protected static ArrayList<Card> cardDeckFromFile(String path, Integer playerCount)
            throws FileNotFoundException, FileNotDeckException, IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) { // The try will close reader
            String currentLine;
            int lineCount = 0;
            HashMap<Integer, Integer> cards = new HashMap<Integer, Integer>();

            ArrayList<Card> deck = new ArrayList<Card>();

            // Read file and put it into cards hashmap
            while ((currentLine = reader.readLine()) != null) {
                lineCount++;

                try {
                    Integer cardFace = Integer.parseInt(currentLine);
                    cards.put(cardFace, cards.getOrDefault(cardFace, 0) + 1);
                    deck.add(new Card(cardFace));

                } catch (NumberFormatException e) {
                    throw new FileNotDeckException(
                            "Error in " + path + " on line " + lineCount + ". Line is not an integer");
                }
            }

            if (lineCount < (8 * playerCount)) { // 8n rows is required
                throw new FileNotDeckException("Not enough lines for provided playerCount in " + path
                        + ". The required is >=" + 8 * playerCount + " provided was " + lineCount);
            } else {
                // Check that each card face has over 4 cards associated with it
                for (Map.Entry<Integer, Integer> card : cards.entrySet()) {
                    if (card.getValue() < 4) {
                        throw new FileNotDeckException("Card " + card.getKey() + " only has " + card.getValue()
                                + " cards in " + path + ", requires 4.");
                    }
                }

                // To get here there must be >8*playercount card types and over 4 of each card
                // type
                return deck;
            }
        }
    }

    /**
     * Notifies all players of game end. Returns `true` if `winningPlayer` is the first/actual winner.
     * @param winningPlayer
     * @return Boolean if `winningPlayer` is the first winner.
     */
    public static Boolean endGame(Player winningPlayer) {
        if (winnerSelected.compareAndSet(false, true)) { // Only one player can use this at a time
            for (int i = 0; i < players.size(); i++) {

                Player player = players.get(i);
                playerThreads.get(i).interrupt();

                if (!player.equals(winningPlayer)) {
                    System.out.printf("%s has informed %s that %s has won%n", winningPlayer.getName(), player.getName(), winningPlayer.getName());
                }
            }

            for (CardDeck deck : decks) {
                deck.endDeck(); // Saves states to file and outputs contents
            }

            return true;
        } else {
            return false;
        }
    }
}

class FileNotDeckException extends Exception {
    /**
     * Custom error constructor for invalid deck files.
     * @param message
     */
    public FileNotDeckException(String message) {
        super(message);
    }
}