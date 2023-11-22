import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class CardGame {
    private final static AtomicBoolean winnerSelected = new AtomicBoolean(false);
    // private final static ArrayList<Player> players;

    public static void main(String[] args) {
        Integer playerCount = 4;
        ArrayList<Card> deck;

        // Getting user input
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

            while (true) {
                System.out.print("\nEnter the card deck path (generate):\n");
                String input = scanner.nextLine();

                if (input.isEmpty()) {
                    System.out.println("The card deck will be generated");
                    // generate it
                    deck = new ArrayList<Card>();
                    deck.add(new Card(0));
                    break;
                }

                try {
                    // Path filePath = Paths.get(input);
                    // if (filePath.isAbsolute() && filePath.toFile().exists()) {
                    // break;
                    // } else {
                    // System.out.println("\nInvalid input. Perhaps try the full file path?");
                    // }
                    deck = cardDeckFromFile(input, playerCount);
                    break;

                } catch (FileNotFoundException e) {
                    System.out.println("\nInvalid input. The file could not be found:\n" + e);
                } catch(FileNotDeckException e) {
                    System.out.println("\nInvalid input. The file was an invalid deck:\n" + e);
                } catch (IOException e) {
                    System.out.println("\nInvalid input.\n" + e);
                }
            }
        }

        System.out.println(playerCount);
        System.out.println(deck.size());

        // String userPlayerInput = user.nextLine();
        // while (!isInt(userPlayerInput)) {
        // }
    }

    // for (int i = 1; i<=playerCount; i++) {
    // players.add(new Player(i, null, null));
    // }

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
                            "Error in "+ path + " on line " + lineCount + ". Line is not an integer");
                }
            }

            if (lineCount < (8 * playerCount)) { // 8n rows is required
                throw new FileNotDeckException("Not enough lines for provided playerCount in " + path + ". The required is >=" + 8*playerCount + " provided waws " + cards.size());
            } else {
                // Check that each card face has over 4 cards associated with it
                for (Map.Entry<Integer, Integer> card : cards.entrySet()) {
                    if (card.getValue() < 4) {
                        throw new FileNotDeckException("Card " + card.getKey() + " only has " + card.getValue() + " cards in "+ path +", requires 4.");
                    }
                }

                // To get here there must be >8*playercount card types and over 4 of each card
                // type
                return deck;
            }
        }
    }

    public static void endGame() {
        if (winnerSelected.compareAndSet(false, true)) {

        } else {
            System.out.println("Thread has already been selected as winner");
        }
    }
}

class FileNotDeckException extends Exception {
    public FileNotDeckException(String message) {
        super(message);
    }
}