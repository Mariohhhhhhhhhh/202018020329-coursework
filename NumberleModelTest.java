import static org.junit.Assert.*;
import org.junit.Test;

public class NumberleModelTest {

    @Test
    public void testSetupGame() {
        NumberleModel model = new NumberleModel();
        model.setupGame();
        assertNotNull(model.getCorrectEquation());
        assertNotNull(model.getAttemptsLeft());
        assertFalse(model.isGameEnded());
    }

    @Test
    public void testHandleInputCorrect() {
        NumberleModel model = new NumberleModel();
        model.setupGame();
        assertTrue(model.handleInput(model.getCorrectEquation()));
        assertTrue(model.isGameEnded());
        assertTrue(model.isPlayerWinner());
    }

    @Test
    public void testHandleInputIncorrect() {
        NumberleModel model = new NumberleModel();
        model.setupGame();
        String incorrectInput = "2 + 2 = 5"; // Incorrect equation
        assertTrue(model.handleInput(incorrectInput));
        assertTrue(model.getAttemptsLeft() < NumberleModel.MAX_TRIES); // Attempts should have decreased
        assertFalse(model.isGameEnded());
    }
}
