import java.util.Scanner;

public class CLIApp {
    public static void main(String[] args) {
        NumberleModel gameInstance = new NumberleModel();
        gameInstance.setupGame();
        Scanner inputReader = new Scanner(System.in);

        while (!gameInstance.isGameEnded()) {
            System.out.println("Enter your guess:");
            String playerGuess = inputReader.nextLine().trim();
            boolean inputStatus = gameInstance.handleInput(playerGuess);
            if (playerGuess.length() < 7) {
                System.out.println("Please enter a valid guess.");
                continue; // Restart the loop to prompt the user for a new input
            }

            if (inputStatus) {
                if (gameInstance.isPlayerWinner()) {
                    System.out.println("Well done, correct guess!");
                } else {
                    System.out.println("Partially correct, try again.");
                }
            } else {
                System.out.println("Incorrect guess, try again!");
            }

            // Display current guess feedback
            StringBuilder feedbackBuilder = new StringBuilder();
            String correctEquation = gameInstance.getCorrectEquation();
            for (int i = 0; i < correctEquation.length(); i++) {
                if (i < playerGuess.length() && playerGuess.charAt(i) == correctEquation.charAt(i)) {
                    feedbackBuilder.append(playerGuess.charAt(i));
                } else if (correctEquation.contains(Character.toString(playerGuess.charAt(i)))) {
                    feedbackBuilder.append("?");
                } else {
                    feedbackBuilder.append("_");
                }
            }
            System.out.println("Current guess status: " + feedbackBuilder.toString());
            System.out.println("Attempts left: " + gameInstance.getAttemptsLeft());
            System.out.println();
        }

        // End of game message
        if (gameInstance.isPlayerWinner()) {
            System.out.println("Correct equation was: " + gameInstance.getCorrectEquation());
            System.out.println("Congratulations, you won!");
        } else {
            System.out.println("Correct equation was: " + gameInstance.getCorrectEquation());
            System.out.println("Sorry, you lost the game!");
        }
    }
}
