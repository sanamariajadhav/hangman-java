// ============================================================
// HangmanFX.java
// The JavaFX User Interface for the Hangman Game.
// This is the main entry point for the FX version.
//
// HOW TO RUN:
//   Make sure you have JavaFX installed.
//   Compile all .java files, then run HangmanFX.
//
// SCREENS:
//   1. Welcome Screen  - enter name
//   2. Difficulty Screen - pick easy/medium/hard
//   3. Game Screen     - play the game with letter buttons
//   4. Result Screen   - win/lose + scoreboard
// ============================================================

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class HangmanFX extends Application {

    // --- Game objects ---
    private Player player;
    private WordLoader wordLoader;
    private HangmanGameFX game;

    // --- UI elements we need to update during gameplay ---
    private Stage primaryStage;
    private Canvas hangmanCanvas;
    private Label wordLabel;
    private Label guessedLabel;
    private Label wrongGuessesLabel;
    private Label messageLabel;
    private Label scoreLabel;
    private GridPane letterGrid;
    private String currentDifficulty;

    // -------------------------------------------------------
    // start() - JavaFX entry point, called automatically
    // -------------------------------------------------------
    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Hangman Game");
        stage.setResizable(false);
        showWelcomeScreen();
        stage.show();
    }

    // -------------------------------------------------------
    // SCREEN 1: Welcome Screen
    // Player enters their name here
    // -------------------------------------------------------
    private void showWelcomeScreen() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #1a1a2e;");

        Label title = new Label("🎮 HANGMAN GAME");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        title.setTextFill(Color.web("#e94560"));

        Label subtitle = new Label("Guess the word before it's too late!");
        subtitle.setFont(Font.font("Arial", 16));
        subtitle.setTextFill(Color.LIGHTGRAY);

        Label nameLabel = new Label("Enter your name:");
        nameLabel.setFont(Font.font("Arial", 18));
        nameLabel.setTextFill(Color.WHITE);

        TextField nameField = new TextField();
        nameField.setMaxWidth(250);
        nameField.setFont(Font.font("Arial", 16));
        nameField.setStyle("-fx-background-color: #16213e; -fx-text-fill: white; -fx-border-color: #e94560; -fx-border-radius: 5; -fx-background-radius: 5;");
        nameField.setPromptText("Your name here...");

        Label hint = new Label("Tip: * means that letter is a vowel (a,e,i,o,u)");
        hint.setFont(Font.font("Arial", 13));
        hint.setTextFill(Color.GOLD);

        Button startBtn = new Button("START GAME");
        startBtn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        startBtn.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 10 30;");

        Label errorLabel = new Label("");
        errorLabel.setTextFill(Color.RED);

        startBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                errorLabel.setText("Please enter your name!");
                return;
            }
            // Create player and game objects
            player = new Player(name);
            wordLoader = new WordLoader("words.txt");
            game = new HangmanGameFX(player, wordLoader);
            showDifficultyScreen();
        });

        // Allow pressing Enter to start
        nameField.setOnAction(e -> startBtn.fire());

        root.getChildren().addAll(title, subtitle, new Label(""), nameLabel, nameField, hint, startBtn, errorLabel);
        primaryStage.setScene(new Scene(root, 500, 420));
    }

    // -------------------------------------------------------
    // SCREEN 2: Difficulty Selection Screen
    // -------------------------------------------------------
    private void showDifficultyScreen() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #1a1a2e;");

        Label title = new Label("Choose Difficulty");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#e94560"));

        Label playerLabel = new Label("Player: " + player.getName() + "  |  Score: " + game.getScore());
        playerLabel.setFont(Font.font("Arial", 16));
        playerLabel.setTextFill(Color.LIGHTGRAY);

        // Easy button
        Button easyBtn = makeDifficultyButton("🟢  EASY  (4-5 letter words)", "#27ae60");
        Button medBtn  = makeDifficultyButton("🟡  MEDIUM  (6-7 letter words)", "#f39c12");
        Button hardBtn = makeDifficultyButton("🔴  HARD  (8+ letter words)", "#c0392b");

        Label errorLabel = new Label("");
        errorLabel.setTextFill(Color.RED);

        easyBtn.setOnAction(e -> startGame("easy", errorLabel));
        medBtn.setOnAction(e  -> startGame("medium", errorLabel));
        hardBtn.setOnAction(e -> startGame("hard", errorLabel));

        root.getChildren().addAll(title, playerLabel, new Label(""), easyBtn, medBtn, hardBtn, errorLabel);
        primaryStage.setScene(new Scene(root, 500, 400));
    }

    // Helper to create styled difficulty buttons
    private Button makeDifficultyButton(String text, String color) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        btn.setMaxWidth(280);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 12 30;");
        return btn;
    }

    // Called when a difficulty is selected
    private void startGame(String difficulty, Label errorLabel) {
        currentDifficulty = difficulty;
        boolean success = game.startNewRound(difficulty);
        if (!success) {
            errorLabel.setText("No more new words for this difficulty! Try another.");
            return;
        }
        showGameScreen();
    }

    // -------------------------------------------------------
    // SCREEN 3: Main Game Screen
    // -------------------------------------------------------
    private void showGameScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a2e;");
        root.setPadding(new Insets(15));

        // --- TOP: Title and score ---
        VBox topBox = new VBox(5);
        topBox.setAlignment(Pos.CENTER);
        Label title = new Label("HANGMAN");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#e94560"));
        scoreLabel = new Label("Player: " + player.getName() + "  |  Score: " + game.getScore());
        scoreLabel.setFont(Font.font("Arial", 14));
        scoreLabel.setTextFill(Color.LIGHTGRAY);
        topBox.getChildren().addAll(title, scoreLabel);
        root.setTop(topBox);

        // --- LEFT: Hangman drawing ---
        hangmanCanvas = new Canvas(200, 220);
        drawHangman(0); // start with empty gallows
        VBox leftBox = new VBox(hangmanCanvas);
        leftBox.setAlignment(Pos.CENTER);
        leftBox.setPadding(new Insets(10));
        root.setLeft(leftBox);

        // --- CENTER: Word, messages, guesses ---
        VBox centerBox = new VBox(12);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(10));

        wordLabel = new Label(getDisplayString());
        wordLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 28));
        wordLabel.setTextFill(Color.WHITE);

        wrongGuessesLabel = new Label("Wrong guesses left: " + game.getWrongGuessesLeft());
        wrongGuessesLabel.setFont(Font.font("Arial", 14));
        wrongGuessesLabel.setTextFill(Color.ORANGE);

        guessedLabel = new Label("Letters guessed: none");
        guessedLabel.setFont(Font.font("Arial", 13));
        guessedLabel.setTextFill(Color.LIGHTGRAY);

        messageLabel = new Label("");
        messageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        messageLabel.setTextFill(Color.YELLOW);

        centerBox.getChildren().addAll(wordLabel, wrongGuessesLabel, guessedLabel, messageLabel);
        root.setCenter(centerBox);

        // --- BOTTOM: Letter buttons A-Z ---
        letterGrid = new GridPane();
        letterGrid.setAlignment(Pos.CENTER);
        letterGrid.setHgap(5);
        letterGrid.setVgap(5);
        letterGrid.setPadding(new Insets(10));

        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int col = 0, row = 0;
        for (char c : alphabet.toCharArray()) {
            Button btn = new Button(String.valueOf(c));
            btn.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            btn.setPrefSize(38, 38);
            btn.setStyle("-fx-background-color: #16213e; -fx-text-fill: white; -fx-border-color: #e94560; -fx-border-radius: 4; -fx-background-radius: 4;");

            final char letter = Character.toLowerCase(c);
            btn.setOnAction(e -> handleGuess(letter, btn));

            letterGrid.add(btn, col, row);
            col++;
            if (col == 9) { col = 0; row++; } // 9 buttons per row
        }

        root.setBottom(letterGrid);
        primaryStage.setScene(new Scene(root, 650, 550));
    }

    // -------------------------------------------------------
    // handleGuess()
    // Called when a letter button is clicked
    // -------------------------------------------------------
    private void handleGuess(char letter, Button btn) {
        String result = game.guessLetter(letter);

        if (result.equals("already")) {
            messageLabel.setText("⚠ You already guessed '" + Character.toUpperCase(letter) + "'!");
            messageLabel.setTextFill(Color.YELLOW);
            return; // do NOT disable the button or update hangman
        }

        // Disable and colour the button
        btn.setDisable(true);
        if (result.equals("correct") || result.equals("won")) {
            btn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 4;");
        } else {
            btn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-background-radius: 4;");
        }

        // Update all UI labels
        wordLabel.setText(getDisplayString());
        wrongGuessesLabel.setText("Wrong guesses left: " + game.getWrongGuessesLeft());

        // Update guessed letters display
        StringBuilder guessed = new StringBuilder("Letters guessed: ");
        for (char g : game.getGuessedLetters()) {
            guessed.append(Character.toUpperCase(g)).append(" ");
        }
        guessedLabel.setText(guessed.toString());

        // Update hangman drawing
        int stage = HangmanGameFX.MAX_WRONG_GUESSES - game.getWrongGuessesLeft();
        drawHangman(stage);

        // Handle end of game
        if (result.equals("won")) {
            messageLabel.setText("🎉 You won! The word was: " + game.getWordToGuess().toUpperCase());
            messageLabel.setTextFill(Color.LIGHTGREEN);
            scoreLabel.setText("Player: " + player.getName() + "  |  Score: " + game.getScore());
            disableAllButtons();
            showResultScreen(true);
        } else if (result.equals("lost")) {
            messageLabel.setText("💀 Game over! The word was: " + game.getWordToGuess().toUpperCase());
            messageLabel.setTextFill(Color.RED);
            disableAllButtons();
            showResultScreen(false);
        } else if (result.equals("correct")) {
            messageLabel.setText("✅ Correct!");
            messageLabel.setTextFill(Color.LIGHTGREEN);
        } else {
            messageLabel.setText("❌ Wrong guess!");
            messageLabel.setTextFill(Color.ORANGE);
        }
    }

    // Disable all letter buttons (called at end of game)
    private void disableAllButtons() {
        for (javafx.scene.Node node : letterGrid.getChildren()) {
            if (node instanceof Button) {
                node.setDisable(true);
            }
        }
    }

    // -------------------------------------------------------
    // SCREEN 4: Result Screen (win or lose)
    // Shows score and asks to play again
    // -------------------------------------------------------
    private void showResultScreen(boolean won) {
        // Small delay so the player sees the final game state first
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.5));
        pause.setOnFinished(e -> {
            VBox root = new VBox(18);
            root.setAlignment(Pos.CENTER);
            root.setPadding(new Insets(40));
            root.setStyle("-fx-background-color: #1a1a2e;");

            // Result message
            Label resultLabel = new Label(won ? "🎉 YOU WIN!" : "💀 GAME OVER!");
            resultLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
            resultLabel.setTextFill(won ? Color.LIGHTGREEN : Color.web("#e94560"));

            Label wordReveal = new Label("The word was: " + game.getWordToGuess().toUpperCase());
            wordReveal.setFont(Font.font("Arial", 18));
            wordReveal.setTextFill(Color.WHITE);

            // Scoreboard
            Label scoreboardTitle = new Label("═══ LEADERBOARD ═══");
            scoreboardTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            scoreboardTitle.setTextFill(Color.GOLD);

            Label scoreEntry = new Label(String.format("%-20s %d point(s)", player.getName(), game.getScore()));
            scoreEntry.setFont(Font.font("Courier New", 16));
            scoreEntry.setTextFill(Color.WHITE);

            // Play again / quit buttons
            HBox btnBox = new HBox(20);
            btnBox.setAlignment(Pos.CENTER);

            Button playAgainBtn = new Button("🔄 Play Again");
            playAgainBtn.setFont(Font.font("Arial", FontWeight.BOLD, 15));
            playAgainBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 10 25;");

            Button quitBtn = new Button("❌ Quit");
            quitBtn.setFont(Font.font("Arial", FontWeight.BOLD, 15));
            quitBtn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 10 25;");

            playAgainBtn.setOnAction(ev -> showDifficultyScreen());
            quitBtn.setOnAction(ev -> {
                showFinalGoodbye();
            });

            btnBox.getChildren().addAll(playAgainBtn, quitBtn);
            root.getChildren().addAll(resultLabel, wordReveal, new Label(""), scoreboardTitle, scoreEntry, new Label(""), btnBox);
            primaryStage.setScene(new Scene(root, 500, 400));
        });
        pause.play();
    }

    // Final goodbye screen when player quits
    private void showFinalGoodbye() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #1a1a2e;");

        Label bye = new Label("Thanks for playing, " + player.getName() + "!");
        bye.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        bye.setTextFill(Color.WHITE);

        Label finalScore = new Label("Final Score: " + game.getScore() + " point(s)");
        finalScore.setFont(Font.font("Arial", 20));
        finalScore.setTextFill(Color.GOLD);

        Button closeBtn = new Button("Close Game");
        closeBtn.setFont(Font.font("Arial", 15));
        closeBtn.setStyle("-fx-background-color: #e94560; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 10 25;");
        closeBtn.setOnAction(e -> primaryStage.close());

        root.getChildren().addAll(bye, finalScore, closeBtn);
        primaryStage.setScene(new Scene(root, 450, 280));
    }

    // -------------------------------------------------------
    // getDisplayString()
    // Converts the char[] displayWord into a spaced string
    // e.g. ['_', '*', '_'] → "_ * _"
    // -------------------------------------------------------
    private String getDisplayString() {
        StringBuilder sb = new StringBuilder();
        for (char c : game.getDisplayWord()) {
            sb.append(c).append("  ");
        }
        return sb.toString().trim();
    }

    // -------------------------------------------------------
    // drawHangman()
    // Draws the hangman on the Canvas using JavaFX shapes.
    // stage = number of wrong guesses made (0 to 6)
    // -------------------------------------------------------
    private void drawHangman(int stage) {
        GraphicsContext gc = hangmanCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, 200, 220); // clear previous drawing

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);

        // Gallows structure (always shown)
        gc.strokeLine(20, 210, 180, 210); // base
        gc.strokeLine(60, 210, 60, 20);   // pole
        gc.strokeLine(60, 20, 130, 20);   // top bar
        gc.strokeLine(130, 20, 130, 50);  // rope

        gc.setStroke(Color.web("#e94560"));
        gc.setLineWidth(2.5);

        // Stage 1: Head
        if (stage >= 1) gc.strokeOval(110, 50, 40, 40);

        // Stage 2: Body
        if (stage >= 2) gc.strokeLine(130, 90, 130, 150);

        // Stage 3: Left arm
        if (stage >= 3) gc.strokeLine(130, 100, 100, 130);

        // Stage 4: Right arm
        if (stage >= 4) gc.strokeLine(130, 100, 160, 130);

        // Stage 5: Left leg
        if (stage >= 5) gc.strokeLine(130, 150, 100, 185);

        // Stage 6: Right leg (game over)
        if (stage >= 6) gc.strokeLine(130, 150, 160, 185);
    }

    // -------------------------------------------------------
    // main() - launches the JavaFX app
    // -------------------------------------------------------
    public static void main(String[] args) {
        launch(args);
    }
}
