import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class CardGame {
    private final static AtomicBoolean winnerSelected = new AtomicBoolean(false);
    private final static ArrayList<Player> players = new ArrayList<Player>();
    private final static ArrayList<CardDeck> decks = new ArrayList<CardDeck>();

    public static void main(String[] args) {
        Integer playerCount = 4;
        ArrayList<Card> deck; // NOTE: deck and decks. completeDeck may have been better

        // Getting user input
        // I recomend just collapsing this in editor
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Input nothing for defaults");

            // Get playercount from user
            while (true) {
                System.out.print("Enter the number of players (4):\n");
                String input = scanner.nextLine();

                if (input.isEmpty()) {
                    System.out.printf("Default of %s has been selected.%n", playerCount.toString());
                    break;
                }

                try {
                    playerCount = Integer.parseInt(input);
                    // System.out.printf("%n%s players", playerCount.toString());
                    if (playerCount < 2) {
                        System.out.println("\nInvalid input. There must be at least 2 players");
                    } else {
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("\nInvalid input.");
                }
            }

            // Get card deck
            while (true) {
                System.out.print("\nEnter the card deck path (generate):\n");
                String input = scanner.nextLine();

                if (input.isEmpty()) {
                    System.out.println("The card deck will be generated");

                    deck = generateCardDeck(playerCount);
                    break;
                }

                try {
                    deck = cardDeckFromFile(input, playerCount);
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

        Collections.shuffle(deck);

        // Generate decks and distribute cards
        int cardIndex = 0;
        for (int deck_i = 1; deck_i <= playerCount; deck_i++) {
            Card[] startingDeck = {deck.get(cardIndex), deck.get(cardIndex+1), deck.get(cardIndex+2), deck.get(cardIndex+3)};
            // You can not put Card[] directy into a method for some reason. Hence ^
            decks.add(new CardDeck(deck_i, startingDeck));
            cardIndex += 4;
        }

        // Generate players and distribute cards
        for (int player_i = 1; player_i <= playerCount; player_i++) {
            if (playerCount.equals(player_i)) { // Last player loops around
                Card[] startingHand = {deck.get(cardIndex), deck.get(cardIndex+1), deck.get(cardIndex+2), deck.get(cardIndex+3)};
                players.add(new Player(player_i, decks.getLast(), decks.getFirst(), startingHand));
            } else { // All other players
                Card[] startingHand = {deck.get(cardIndex), deck.get(cardIndex+1), deck.get(cardIndex+2), deck.get(cardIndex+3)};
                players.add(new Player(player_i, decks.get(player_i-1), decks.get(player_i), startingHand));
            }
            cardIndex += 4;
        }


        // I could start the players above but i think that is asking for problems
        for (Player player : players) {
            player.start();
        }
    }

    private static ArrayList<Card> generateCardDeck(int playerCount) {
        return generateCardDeck("./generated-deck.txt", playerCount);
    }

    private static ArrayList<Card> generateCardDeck(String generatedFilePath, int playerCount) {
        try (FileWriter fileWriter = new FileWriter(generatedFilePath)) {
            ArrayList<Card> deck = new ArrayList<Card>();
            for (int face = 1; face <= (2 * playerCount); face++) { // 4 decks + 4 players (2*playercount), each with 4
                                                                    // cards.
                for (int card = 1; card <= 4; card++) {
                    // System.out.println(card + " of face " + face);
                    fileWriter.write(Integer.toString(face) + "\n");
                    deck.add(new Card(face));
                }
            }
            return deck;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ArrayList<Card> cardDeckFromFile(String path, Integer playerCount)
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

    public static void endGame(Player winningPlayer) {
        if (winnerSelected.compareAndSet(false, true)) { // Only one player can use this at a time
            for (Player player : players) {
                player.stopThread();
                if (!player.equals(winningPlayer)) {
                    System.out.printf("%s has informed %s that %s has won%n", winningPlayer.getName(), player.getName(), winningPlayer.getName());
                }
            }

            for (CardDeck deck : decks) {
                deck.endDeck(); // Saves states to file and outputs contents
            }

            System.out.println(winningPlayer.getName() + " has won!");
        } else {
            System.out.println(winningPlayer.getName() + " attempted to win but a winner has already been chosen.");
        }
    }
}

class FileNotDeckException extends Exception {
    public FileNotDeckException(String message) {
        super(message);
    }
}