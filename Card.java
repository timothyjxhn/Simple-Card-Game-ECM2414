public record Card(int value) {
    public Card {
        if (value < 1) {
            throw new IllegalArgumentException("Card value must be positive, non-zero integer");
        }
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Card) {
            Card other = (Card) obj;
            return value == other.value;
        }
        return false;
    }
}
