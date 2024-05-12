import org.w3c.dom.ls.LSOutput;

import javax.swing.*;

public class GUIApp {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        initializeAndDisplayGUI();
                    }
                }
        );
        
    }

    public static void initializeAndDisplayGUI() {
        INumberleModel gameModel = new NumberleModel();
        NumberleController gameController = new NumberleController(gameModel);
        NumberleView gameView = new NumberleView(gameModel, gameController);
    }
}
