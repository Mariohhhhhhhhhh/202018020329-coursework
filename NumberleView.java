import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class NumberleView implements Observer {
    private final INumberleModel gameModel;
    private final NumberleController gameController;
    private final JFrame mainFrame = new JFrame("Numberle");
    private final JTextField[][] guessFields = new JTextField[6][7];
    private final JLabel attemptsLabel = new JLabel("Tries left: ");
    private final JButton restartGameButton = new JButton("Restart Game");
    private final JPanel inputPanel = new JPanel();
    private JFrame winMessageFrame;
    private JFrame loseMessageFrame;
    private int activeRow = 0;

    public NumberleView(INumberleModel model, NumberleController controller) {
        this.gameController = controller;
        this.gameModel = model;
        this.gameController.setupNewGame();
        ((NumberleModel) this.gameModel).addObserver(this);
        initializeMainFrame();
        this.gameController.setGameView(this);
        update((NumberleModel) this.gameModel, null);
    }

    public void initializeMainFrame() {
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(600, 600); // Increased height to accommodate more components
        mainFrame.setLayout(new BorderLayout());

        // Create a panel for the top label
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        topPanel.add(attemptsLabel);
        mainFrame.add(topPanel, BorderLayout.NORTH);

        // Center container for the grid
        JPanel centerContainer = new JPanel();
        centerContainer.setLayout(new FlowLayout(FlowLayout.CENTER)); // Changed to FlowLayout
        mainFrame.add(centerContainer, BorderLayout.CENTER);

        JPanel fieldGrid = new JPanel();
        fieldGrid.setLayout(new GridLayout(6, 1));
        for (int i = 0; i < guessFields.length; i++) {
            JPanel rowContainer = new JPanel();
            rowContainer.setLayout(new GridLayout(1, 7));
            for (int j = 0; j < guessFields[i].length; j++) {
                guessFields[i][j] = new JTextField();
                guessFields[i][j].setPreferredSize(new Dimension(50, 50));
                guessFields[i][j].setEditable(false);
                guessFields[i][j].setHorizontalAlignment(JTextField.CENTER); // Center text horizontally
                guessFields[i][j].setFont(new Font("Arial", Font.BOLD, 18)); // Set font to bold
                rowContainer.add(guessFields[i][j]);
            }
            fieldGrid.add(rowContainer);
        }
        centerContainer.add(fieldGrid);

        // Input panel for buttons
        inputPanel.setLayout(new GridLayout(3, 1)); // Changed to GridLayout with 3 rows

        JPanel row1 = new JPanel();
        row1.setLayout(new GridLayout(1, 10));
        for (int i = 1; i <= 10; i++) {
            JButton digitButton = new JButton(Integer.toString(i % 10));
            digitButton.addActionListener(e -> {
                for (int j = 0; j < guessFields[activeRow].length; j++) {
                    if (guessFields[activeRow][j].getText().isEmpty()) {
                        guessFields[activeRow][j].setText(digitButton.getText());
                        return;
                    }
                }
            });
            row1.add(digitButton);
        }
        inputPanel.add(row1);

        JPanel row2 = new JPanel();
        row2.setLayout(new GridLayout(1, 7)); // Adjust to fit an additional button
        String[] operators = {"Backspace", "+", "-", "*", "/", "="};
        for (String operator : operators) {
            JButton operatorButton = new JButton(operator);
            operatorButton.addActionListener(e -> {
                if (operator.equals("Backspace")) {
                    for (int j = guessFields.length - 1; j >= 0; j--) {
                        for (int k = guessFields[j].length - 1; k >= 0; k--) {
                            if (!guessFields[j][k].getText().isEmpty()) {
                                guessFields[j][k].setText("");
                                return;
                            }
                        }
                    }
                } else {
                    for (int j = 0; j < guessFields.length; j++) {
                        for (int k = 0; k < guessFields[j].length; k++) {
                            if (guessFields[j][k].getText().isEmpty()) {
                                guessFields[j][k].setText(operator);
                                return;
                            }
                        }
                    }
                }
            });
            row2.add(operatorButton);
        }

        // Adding Enter button
        JButton enterButton = new JButton("Enter");
        enterButton.addActionListener(e -> submitGuess());
        row2.add(enterButton);

        inputPanel.add(row2);

        JPanel restartPanel = new JPanel();
        restartPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        restartPanel.add(restartGameButton);
        inputPanel.add(restartPanel);

        mainFrame.add(inputPanel, BorderLayout.SOUTH);

        restartGameButton.setEnabled(false);

        restartGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gameController.setupNewGame();
                resetGame();
                restartGameButton.setEnabled(false);
            }
        });

        mainFrame.setVisible(true);

        // Adding key listener to handle Enter key
        mainFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    submitGuess();
                }
            }
        });
        mainFrame.setFocusable(true);
        mainFrame.requestFocusInWindow();
    }

    // Clear all guess fields
    private void clearGuessFields() {
        for (JTextField[] fieldRow : guessFields) {
            for (JTextField field : fieldRow) {
                field.setText("");
            }
        }
    }

    // Refresh the colors of guess fields
    private void refreshGuessFieldColors(int row) {
        String guess = getGuessRowText(row);
        String solution = gameModel.getCorrectEquation();
        int rowLength = guessFields[row].length;
        int maxLength = Math.min(guess.length(), solution.length());
        for (int i = 0; i < rowLength && i < maxLength; i++) {
            char guessedChar = guess.charAt(i);
            char solutionChar = solution.charAt(i);
            if (guessedChar == solutionChar) {
                guessFields[row][i].setBackground(Color.GREEN);
            } else if (solution.contains(String.valueOf(guessedChar))) {
                guessFields[row][i].setBackground(Color.ORANGE);
            } else {
                guessFields[row][i].setBackground(Color.GRAY);
            }
        }
    }

    // Get text of the guess row
    private String getGuessRowText(int row) {
        StringBuilder guessBuilder = new StringBuilder();
        for (JTextField field : guessFields[row]) {
            String fieldText = field.getText();
            if (!fieldText.isEmpty()) {
                guessBuilder.append(fieldText);
            }
        }
        return guessBuilder.toString();
    }

    // Submit the current guess
    private void submitGuess() {
        System.out.println("Submitting row " + (activeRow + 1) + ": " + getGuessRowText(activeRow));
        gameController.handleUserInput(getGuessRowText(activeRow));
        moveToNextRow();
        restartGameButton.setEnabled(true);
        refreshGuessFieldColors(activeRow);
        refreshInputPanelColors();
    }

    @Override
    public void update(Observable o, Object arg) {
        attemptsLabel.setText("Tries left: " + gameController.getAttemptsLeft());
        if (gameModel.isPlayerWinner()) {
            displayWinMessage();
            restartGameButton.setEnabled(true);
        } else if (gameModel.isGameEnded()) {
            displayLoseMessage();
            restartGameButton.setEnabled(true);
        } else {
            refreshGuessFieldColors(activeRow);
        }
        refreshGuessFieldColors(activeRow);
    }

    // Display win message
    public void displayWinMessage() {
        if (winMessageFrame == null) {
            winMessageFrame = new JFrame("Congratulations!");
            JLabel winLabel = new JLabel("Great job! You've won!");
            JButton resetBtn = new JButton("Restart Game");
            resetBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    gameController.setupNewGame();
                    resetGame();
                    disableResetButton();
                    winMessageFrame.dispose();
                }
            });
            JPanel winPanel = new JPanel();
            winPanel.add(winLabel);
            winPanel.add(resetBtn);
            winMessageFrame.add(winPanel);
            winMessageFrame.setSize(300, 100);
            winMessageFrame.setLocationRelativeTo(null);
            winMessageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
        winMessageFrame.setVisible(true);
    }

    // Display lose message
    public void displayLoseMessage() {
        if (loseMessageFrame == null) {
            loseMessageFrame = new JFrame("Game Over!");
            JLabel loseLabel = new JLabel("Sorry, you ran out of tries.");
            JButton resetBtn = new JButton("Restart Game");
            resetBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    gameController.setupNewGame();
                    resetGame();
                    disableResetButton();
                    loseMessageFrame.dispose();
                }
            });

            JPanel losePanel = new JPanel();
            losePanel.add(loseLabel);
            losePanel.add(resetBtn);
            loseMessageFrame.add(losePanel);
            loseMessageFrame.setSize(300, 100);
            loseMessageFrame.setLocationRelativeTo(null);
            loseMessageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
        loseMessageFrame.setVisible(true);
    }

    // Reset guess field colors
    private void resetGuessFieldColors() {
        for (JTextField[] fieldRow : guessFields) {
            for (JTextField field : fieldRow) {
                field.setBackground(null);
            }
        }
    }

    // Move to the next row
    private void moveToNextRow() {
        boolean emptyFieldFound = false;
        for (int i = activeRow; i < guessFields.length; i++) {
            for (int j = 0; j < guessFields[i].length; j++) {
                if (guessFields[i][j].getText().isEmpty()) {
                    guessFields[i][j].requestFocus();
                    activeRow = i;
                    System.out.println("Active row: " + (activeRow + 1));
                    emptyFieldFound = true;
                    break;
                }
            }
            if (emptyFieldFound) {
                break;
            }
        }
    }

    // Reset the game
    private void resetGame() {
        activeRow = 0;
        clearGuessFields();
        resetGuessFieldColors();
        refreshInputPanelColors();
        resetAllFieldColors();
    }

    // Refresh input panel colors
    private void refreshInputPanelColors() {
        String guess = "";
        int row = activeRow - 1;

        if (row < 0) {
            return;
        }

        guess = getGuessRowText(row);
        String solution = gameModel.getCorrectEquation();

        restoreAllFieldColors();

        for (Component component : inputPanel.getComponents()) {
            if (component instanceof JPanel) {
                JPanel rowPanel = (JPanel) component;
                for (Component buttonComponent : rowPanel.getComponents()) {
                    if (buttonComponent instanceof JButton) {
                        JButton button = (JButton) buttonComponent;
                        String buttonText = button.getText();
                        if (!buttonText.isEmpty()) {
                            if (guess.contains(buttonText)) {
                                if (solution.contains(buttonText)) {
                                    if (guess.indexOf(buttonText) == solution.indexOf(buttonText)) {
                                        button.setBackground(Color.GREEN);
                                    } else {
                                        button.setBackground(Color.ORANGE);
                                    }
                                } else {
                                    button.setBackground(Color.GRAY);
                                }
                            }
                        }
                    }
                }
            }
        }

        storeAllFieldColors();
    }

    // Map to store field colors
    private final Map<JTextField, Color> fieldColorMapping = new HashMap<>();

    // Store all field colors
    private void storeAllFieldColors() {
        for (Component component : inputPanel.getComponents()) {
            if (component instanceof JPanel) {
                JPanel rowPanel = (JPanel) component;
                for (Component fieldComponent : rowPanel.getComponents()) {
                    if (fieldComponent instanceof JTextField) {
                        JTextField field = (JTextField) fieldComponent;
                        fieldColorMapping.put(field, field.getBackground());
                    }
                }
            }
        }
    }

    // Restore all field colors
    private void restoreAllFieldColors() {
        for (Component component : inputPanel.getComponents()) {
            if (component instanceof JPanel) {
                JPanel rowPanel = (JPanel) component;
                for (Component fieldComponent : rowPanel.getComponents()) {
                    if (fieldComponent instanceof JTextField) {
                        JTextField field = (JTextField) fieldComponent;
                        if (fieldColorMapping.containsKey(field)) {
                            field.setBackground(fieldColorMapping.get(field));
                        } else {
                            field.setBackground(null);
                        }
                    }
                }
            }
        }
    }

    // Reset all field colors
    private void resetAllFieldColors() {
        for (Component component : inputPanel.getComponents()) {
            if (component instanceof JTextField) {
                JTextField field = (JTextField) component;
                field.setBackground(null);
            } else if (component instanceof JPanel) {
                JPanel rowPanel = (JPanel) component;
                for (Component fieldComponent : rowPanel.getComponents()) {
                    if (fieldComponent instanceof JButton) {
                        JButton button = (JButton) fieldComponent;
                        button.setBackground(null);
                    }
                }
            }
        }
    }

    // Disable the reset button
    private void disableResetButton() {
        restartGameButton.setEnabled(false);
    }
}
