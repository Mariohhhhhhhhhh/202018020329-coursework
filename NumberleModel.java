import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Observable;

public class NumberleModel extends Observable implements INumberleModel {
    private String correctEquation;
    private StringBuilder feedbackBuilder;
    private int attemptsLeft;
    private boolean playerWon;

    @Override
    public void setupGame() {
        // Load equations and select one
        List<String> equationList = fetchEquationsFromFile("equations.txt");
        Random random = new Random();
        correctEquation = equationList.get(random.nextInt(equationList.size()));

        // Initialize attempts
        attemptsLeft = MAX_TRIES;

        // Game not won initially
        playerWon = false;

        // Notify observers about state update
        setChanged();
        notifyObservers();
    }

    // Load equations from file
    private List<String> fetchEquationsFromFile(String filename) {
        List<String> equations = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {
            String equation;
            while ((equation = bufferedReader.readLine()) != null) {
                equations.add(equation.trim());
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle file reading exceptions
        }
        return equations;
    }

    @Override
    public boolean handleInput(String input) {
        attemptsLeft--;

        // Check if input matches correct equation
        if (input.equals(correctEquation)) {
            playerWon = true;
            setChanged();
            notifyObservers();
            return true; // Player wins
        } else {
            // Check if no attempts left
            if (attemptsLeft <= 0) {
                playerWon = false;
                setChanged();
                notifyObservers();
                return false; // Player loses
            } else {
                setChanged();
                notifyObservers();
                return true; // Continue game
            }
        }
    }

    @Override
    public boolean isGameEnded() {
        return attemptsLeft <= 0 || playerWon;
    }

    @Override
    public boolean isPlayerWinner() {
        return playerWon;
    }

    @Override
    public String getCorrectEquation() {
        return correctEquation;
    }

    @Override
    public StringBuilder getFeedbackBuilder() {
        return feedbackBuilder;
    }

    @Override
    public int getAttemptsLeft() {
        return attemptsLeft;
    }
}
