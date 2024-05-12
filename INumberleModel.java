import java.util.List;

public interface INumberleModel {
    int MAX_TRIES = 6;

    void setupGame();
    boolean handleInput(String input);
    boolean isGameEnded();
    boolean isPlayerWinner();
    String getCorrectEquation();
    StringBuilder getFeedbackBuilder();
    int getAttemptsLeft();
}
