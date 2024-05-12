public class NumberleController {
    private INumberleModel gameModel;
    private NumberleView gameView;

    public NumberleController(INumberleModel model) {
        this.gameModel = model;
    }

    public void setGameView(NumberleView view) {
        this.gameView = view;
    }

    public void handleUserInput(String input) {
        gameModel.handleInput(input);
    }

    public boolean isGameEnded() {
        return gameModel.isGameEnded();
    }

    public boolean isPlayerWinner() {
        return gameModel.isPlayerWinner();
    }

    public String getCorrectEquation() {
        return gameModel.getCorrectEquation();
    }

    public StringBuilder getFeedbackBuilder() {
        return gameModel.getFeedbackBuilder();
    }

    public int getAttemptsLeft() {
        return gameModel.getAttemptsLeft();
    }

    public void setupNewGame() {
        gameModel.setupGame();
    }
}
