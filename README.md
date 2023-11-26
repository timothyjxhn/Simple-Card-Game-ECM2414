# ECM2414-CA
## Overview
This is a simple card game that can be played by at least 2 players.
Each player will be dealt 4 cards, and the remainder will be distributed amongst card decks on the table.\
Each card deck will be placed in between two players, and the players will take turns to pick up a card from the deck on their right, while discarding a card to their left.\
The player will continuously pick up cards until they have a full set of cards with the same value.
---
## Prerequisites
Make sure you have the following installed:
- JDK 21.0.1
- JUnit 4.13.1 ([download](https://repo1.maven.org/maven2/junit/junit/4.13.1/junit-4.13.1.jar))
- Hamcrest 1.3 ([download](https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar))

## Run from source code
Code is run through `$ java CardGame.java`.

Once run it will ask for the number of players and input card deck. There is the option of defaults (via inputting nothing).
## Compile to JAR

---
## Tests
### Compile test suite
Run unit tests through `CardGameTestSuite`. All tests have been included in this suite.
1. Navigate to the `src/` directory.
2. Compile the test suite with:
`$ javac -cp .:[path of junit jar] CardGameTestSuite.java`

### Run test suite
Run the test suite with:
`$ java -cp [path of src directory]:[path of junit jar]:[path of hamcrest jar] org.junit.runner.JUnitCore CardGameTestSuite`\
This will run all tests in the suite.

The output will be similar to the following:
```
JUnit version 4.13.1
....................deck1 initial contents: [1, 2, 3, 4]
.deck1 initial contents: [1, 2, 3, 4]
.deck1 initial contents: [1, 2, 3, 4]
deck1 contents: [1, 2, 3, 4]
.deck1 initial contents: [1, 2, 3, 4]

Time: 0.053

OK (23 tests)
```
### Note
1. Use `;` instead of `:` if you are using Windows command line to run.