public class Card {
    final int value;

    public Card(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Card value must be positive");
        }
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
