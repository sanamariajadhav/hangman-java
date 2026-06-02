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
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class HangmanFX extends Application {

    // ---- Palette constants ----
    private static final String BG_DEEP    = "#111111";
    private static final String BG_SURFACE = "#1c1c1c";
    private static final String BG_RAISED  = "#2a2a2a";
    private static final String ACCENT     = "#6C63FF";   // indigo-violet
    private static final String ACCENT_LT  = "#8B82FF";   // lighter violet for hover text
    private static final String TEXT_PRI   = "#E8E8E8";   // near-white
    private static final String TEXT_SEC   = "#999999";   // mid-grey
    private static final String TEXT_DIM   = "#555555";   // disabled / dim
    private static final String CORRECT    = "#5A5490";   // muted violet for correct letters
    private static final String WRONG_BG   = "#2e1f1f";   // dark maroon for wrong letters
    private static final String WRONG_FG   = "#7a3535";   // muted red text for wrong letters
    private static final String RULE       = "#2e2e2e";   // subtle separator line colour

    // ---- Game objects ----
    private Player player;
    private WordLoader wordLoader;
    private HangmanGameFX game;

    // ---- UI elements updated during gameplay ----
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
    // start() - JavaFX entry point
    // -------------------------------------------------------
    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Hangman");
        stage.setResizable(false);
        showWelcomeScreen();
        stage.show();
    }

    // -------------------------------------------------------
    // SCREEN 1: Welcome Screen
    // -------------------------------------------------------
    private void showWelcomeScreen() {
        VBox root = new VBox(22);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50, 60, 50, 60));
        root.setStyle("-fx-background-color: " + BG_DEEP + ";");

        Label title = new Label("HANGMAN");
        title.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 44));
        title.setTextFill(Color.web(ACCENT));

        // Thin horizontal rule under title
        Separator rule1 = new Separator();
        rule1.setMaxWidth(200);
        rule1.setStyle("-fx-background-color: " + RULE + ";");

        Label subtitle = new Label("Deduce the word before chances run out.");
        subtitle.setFont(Font.font("Times New Roman", FontPosture.ITALIC, 15));
        subtitle.setTextFill(Color.web(TEXT_SEC));

        Label nameLabel = new Label("Player Name");
        nameLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, 14));
        nameLabel.setTextFill(Color.web(TEXT_PRI));

        TextField nameField = new TextField();
        nameField.setMaxWidth(280);
        nameField.setFont(Font.font("Times New Roman", 15));
        nameField.setStyle(
                "-fx-background-color: " + BG_SURFACE + ";" +
                        "-fx-text-fill: " + TEXT_PRI + ";" +
                        "-fx-border-color: " + RULE + ";" +
                        "-fx-border-radius: 2;" +
                        "-fx-background-radius: 2;" +
                        "-fx-padding: 7 10;"
        );
        nameField.setPromptText("Enter your name...");

        Label hint = new Label("Note: * denotes a vowel position (a, e, i, o, u)");
        hint.setFont(Font.font("Times New Roman", FontPosture.ITALIC, 12));
        hint.setTextFill(Color.web(TEXT_DIM));

        Button startBtn = new Button("BEGIN");
        startBtn.setFont(Font.font("Times New Roman", FontWeight.BOLD, 15));
        startBtn.setStyle(
                "-fx-background-color: " + ACCENT + ";" +
                        "-fx-text-fill: " + TEXT_PRI + ";" +
                        "-fx-background-radius: 2;" +
                        "-fx-padding: 9 40;" +
                        "-fx-cursor: hand;"
        );

        Label errorLabel = new Label("");
        errorLabel.setFont(Font.font("Times New Roman", FontPosture.ITALIC, 13));
        errorLabel.setTextFill(Color.web(WRONG_FG));

        startBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                errorLabel.setText("A name is required to continue.");
                return;
            }
            player = new Player(name);
            wordLoader = new WordLoader("words.txt");
            game = new HangmanGameFX(player, wordLoader);
            showDifficultyScreen();
        });

        nameField.setOnAction(e -> startBtn.fire());

        VBox nameGroup = new VBox(6, nameLabel, nameField);
        nameGroup.setAlignment(Pos.CENTER_LEFT);
        nameGroup.setMaxWidth(280);

        root.getChildren().addAll(title, rule1, subtitle, new Label(""), nameGroup, hint, startBtn, errorLabel);
        primaryStage.setScene(new Scene(root, 520, 440));
    }

    // -------------------------------------------------------
    // SCREEN 2: Difficulty Selection
    // -------------------------------------------------------
    private void showDifficultyScreen() {
        VBox root = new VBox(14);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50, 60, 50, 60));
        root.setStyle("-fx-background-color: " + BG_DEEP + ";");

        Label title = new Label("Select Difficulty");
        title.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 30));
        title.setTextFill(Color.web(ACCENT));

        Separator rule1 = new Separator();
        rule1.setMaxWidth(200);
        rule1.setStyle("-fx-background-color: " + RULE + ";");

        Label playerLabel = new Label(player.getName() + "   |   Score: " + game.getScore());
        playerLabel.setFont(Font.font("Times New Roman", FontPosture.ITALIC, 14));
        playerLabel.setTextFill(Color.web(TEXT_SEC));

        Button easyBtn  = makeDifficultyButton("Easy",   "4 – 5 letter words");
        Button medBtn   = makeDifficultyButton("Medium", "6 – 7 letter words");
        Button hardBtn  = makeDifficultyButton("Hard",   "8 or more letters");

        Label errorLabel = new Label("");
        errorLabel.setFont(Font.font("Times New Roman", FontPosture.ITALIC, 13));
        errorLabel.setTextFill(Color.web(WRONG_FG));

        easyBtn.setOnAction(e -> startGame("easy", errorLabel));
        medBtn.setOnAction(e  -> startGame("medium", errorLabel));
        hardBtn.setOnAction(e -> startGame("hard", errorLabel));

        root.getChildren().addAll(title, rule1, playerLabel, new Label(""), easyBtn, medBtn, hardBtn, errorLabel);
        primaryStage.setScene(new Scene(root, 520, 420));
    }

    private Button makeDifficultyButton(String label, String sub) {
        // Custom button: label on left, sub-description on right — all in a HBox
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Times New Roman", FontWeight.BOLD, 15));
        lbl.setTextFill(Color.web(TEXT_PRI));

        Label desc = new Label(sub);
        desc.setFont(Font.font("Times New Roman", FontPosture.ITALIC, 13));
        desc.setTextFill(Color.web(TEXT_SEC));

        // We still return a real Button; put a wrapper label string in it
        Button btn = new Button(label + "     \u2014     " + sub);
        btn.setFont(Font.font("Times New Roman", 14));
        btn.setMaxWidth(320);
        btn.setStyle(
                "-fx-background-color: " + BG_SURFACE + ";" +
                        "-fx-text-fill: " + TEXT_PRI + ";" +
                        "-fx-border-color: " + RULE + ";" +
                        "-fx-border-radius: 2;" +
                        "-fx-background-radius: 2;" +
                        "-fx-padding: 12 24;" +
                        "-fx-cursor: hand;" +
                        "-fx-alignment: center-left;"
        );
        return btn;
    }

    private void startGame(String difficulty, Label errorLabel) {
        currentDifficulty = difficulty;
        boolean success = game.startNewRound(difficulty);
        if (!success) {
            errorLabel.setText("No new words available for this level. Please try another.");
            return;
        }
        showGameScreen();
    }

    // -------------------------------------------------------
    // SCREEN 3: Main Game Screen
    // -------------------------------------------------------
    private void showGameScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_DEEP + ";");
        root.setPadding(new Insets(16));

        // --- TOP: Title bar ---
        VBox topBox = new VBox(4);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(0, 0, 10, 0));

        Label title = new Label("HANGMAN");
        title.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 26));
        title.setTextFill(Color.web(ACCENT));

        scoreLabel = new Label(player.getName() + "   |   Score: " + game.getScore());
        scoreLabel.setFont(Font.font("Times New Roman", FontPosture.ITALIC, 13));
        scoreLabel.setTextFill(Color.web(TEXT_SEC));

        Separator topRule = new Separator();
        topRule.setStyle("-fx-background-color: " + RULE + ";");

        topBox.getChildren().addAll(title, scoreLabel, topRule);
        root.setTop(topBox);

        // --- LEFT: Hangman canvas ---
        hangmanCanvas = new Canvas(200, 220);
        drawHangman(0);
        VBox leftBox = new VBox(hangmanCanvas);
        leftBox.setAlignment(Pos.CENTER);
        leftBox.setPadding(new Insets(10, 0, 0, 0));
        root.setLeft(leftBox);

        // --- CENTER: Word display + info ---
        VBox centerBox = new VBox(14);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(10, 20, 0, 20));

        wordLabel = new Label(getDisplayString());
        wordLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, 30));
        wordLabel.setTextFill(Color.web(TEXT_PRI));
        wordLabel.setStyle("-fx-letter-spacing: 4;");

        Separator wordRule = new Separator();
        wordRule.setMaxWidth(220);
        wordRule.setStyle("-fx-background-color: " + ACCENT + "; -fx-opacity: 0.5;");

        wrongGuessesLabel = new Label("Remaining guesses: " + game.getWrongGuessesLeft());
        wrongGuessesLabel.setFont(Font.font("Times New Roman", 14));
        wrongGuessesLabel.setTextFill(Color.web(TEXT_SEC));

        guessedLabel = new Label("Letters tried: —");
        guessedLabel.setFont(Font.font("Times New Roman", FontPosture.ITALIC, 13));
        guessedLabel.setTextFill(Color.web(TEXT_DIM));

        messageLabel = new Label("");
        messageLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, 14));
        messageLabel.setTextFill(Color.web(ACCENT_LT));

        centerBox.getChildren().addAll(wordLabel, wordRule, wrongGuessesLabel, guessedLabel, messageLabel);
        root.setCenter(centerBox);

        // --- BOTTOM: Letter buttons A–Z ---
        letterGrid = new GridPane();
        letterGrid.setAlignment(Pos.CENTER);
        letterGrid.setHgap(5);
        letterGrid.setVgap(5);
        letterGrid.setPadding(new Insets(12, 0, 4, 0));

        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int col = 0, row = 0;
        for (char c : alphabet.toCharArray()) {
            Button btn = new Button(String.valueOf(c));
            btn.setFont(Font.font("Times New Roman", FontWeight.BOLD, 13));
            btn.setPrefSize(40, 36);
            btn.setStyle(
                    "-fx-background-color: " + BG_RAISED + ";" +
                            "-fx-text-fill: " + TEXT_PRI + ";" +
                            "-fx-border-color: " + RULE + ";" +
                            "-fx-border-radius: 2;" +
                            "-fx-background-radius: 2;" +
                            "-fx-cursor: hand;"
            );

            final char letter = Character.toLowerCase(c);
            btn.setOnAction(e -> handleGuess(letter, btn));

            letterGrid.add(btn, col, row);
            col++;
            if (col == 9) { col = 0; row++; }
        }

        VBox bottomWrapper = new VBox(letterGrid);
        bottomWrapper.setAlignment(Pos.CENTER);
        Separator bottomRule = new Separator();
        bottomRule.setStyle("-fx-background-color: " + RULE + ";");
        bottomWrapper.getChildren().add(0, bottomRule);

        root.setBottom(bottomWrapper);
        primaryStage.setScene(new Scene(root, 670, 560));
    }

    // -------------------------------------------------------
    // handleGuess() - called when a letter button is clicked
    // -------------------------------------------------------
    private void handleGuess(char letter, Button btn) {
        String result = game.guessLetter(letter);

        if (result.equals("already")) {
            messageLabel.setText("'" + Character.toUpperCase(letter) + "' has already been tried.");
            messageLabel.setTextFill(Color.web(TEXT_SEC));
            return;
        }

        btn.setDisable(true);
        if (result.equals("correct") || result.equals("won")) {
            btn.setStyle(
                    "-fx-background-color: " + CORRECT + ";" +
                            "-fx-text-fill: " + TEXT_PRI + ";" +
                            "-fx-border-color: " + ACCENT + ";" +
                            "-fx-border-radius: 2;" +
                            "-fx-background-radius: 2;"
            );
        } else {
            btn.setStyle(
                    "-fx-background-color: " + WRONG_BG + ";" +
                            "-fx-text-fill: " + WRONG_FG + ";" +
                            "-fx-border-color: " + WRONG_BG + ";" +
                            "-fx-border-radius: 2;" +
                            "-fx-background-radius: 2;"
            );
        }

        wordLabel.setText(getDisplayString());
        wrongGuessesLabel.setText("Remaining guesses: " + game.getWrongGuessesLeft());

        StringBuilder guessed = new StringBuilder("Letters tried: ");
        for (char g : game.getGuessedLetters()) {
            guessed.append(Character.toUpperCase(g)).append("  ");
        }
        guessedLabel.setText(guessed.toString().trim());

        int stage = HangmanGameFX.MAX_WRONG_GUESSES - game.getWrongGuessesLeft();
        drawHangman(stage);

        if (result.equals("won")) {
            messageLabel.setText("Correct — the word was: " + game.getWordToGuess().toUpperCase());
            messageLabel.setTextFill(Color.web(ACCENT_LT));
            scoreLabel.setText(player.getName() + "   |   Score: " + game.getScore());
            disableAllButtons();
            showResultScreen(true);
        } else if (result.equals("lost")) {
            messageLabel.setText("The word was: " + game.getWordToGuess().toUpperCase());
            messageLabel.setTextFill(Color.web(WRONG_FG));
            disableAllButtons();
            showResultScreen(false);
        } else if (result.equals("correct")) {
            messageLabel.setText("Correct.");
            messageLabel.setTextFill(Color.web(ACCENT_LT));
        } else {
            messageLabel.setText("Incorrect guess.");
            messageLabel.setTextFill(Color.web(WRONG_FG));
        }
    }

    private void disableAllButtons() {
        for (javafx.scene.Node node : letterGrid.getChildren()) {
            if (node instanceof Button) {
                node.setDisable(true);
            }
        }
    }

    // -------------------------------------------------------
    // SCREEN 4: Result Screen
    // -------------------------------------------------------
    private void showResultScreen(boolean won) {
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.5));
        pause.setOnFinished(e -> {
            VBox root = new VBox(18);
            root.setAlignment(Pos.CENTER);
            root.setPadding(new Insets(50, 60, 50, 60));
            root.setStyle("-fx-background-color: " + BG_DEEP + ";");

            Label resultLabel = new Label(won ? "Victory" : "Defeated");
            resultLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 38));
            resultLabel.setTextFill(won ? Color.web(ACCENT) : Color.web(WRONG_FG));

            Separator rule1 = new Separator();
            rule1.setMaxWidth(160);
            rule1.setStyle("-fx-background-color: " + RULE + ";");

            Label wordReveal = new Label("The word was:  " + game.getWordToGuess().toUpperCase());
            wordReveal.setFont(Font.font("Times New Roman", 17));
            wordReveal.setTextFill(Color.web(TEXT_PRI));

            // Leaderboard panel
            VBox board = new VBox(8);
            board.setAlignment(Pos.CENTER_LEFT);
            board.setPadding(new Insets(14, 24, 14, 24));
            board.setMaxWidth(320);
            board.setStyle(
                    "-fx-background-color: " + BG_SURFACE + ";" +
                            "-fx-border-color: " + RULE + ";" +
                            "-fx-border-radius: 2;" +
                            "-fx-background-radius: 2;"
            );

            Label boardTitle = new Label("SCORE");
            boardTitle.setFont(Font.font("Times New Roman", FontWeight.BOLD, 12));
            boardTitle.setTextFill(Color.web(TEXT_SEC));

            Label scoreEntry = new Label(player.getName() + "   —   " + game.getScore() + " point(s)");
            scoreEntry.setFont(Font.font("Times New Roman", 16));
            scoreEntry.setTextFill(Color.web(TEXT_PRI));

            board.getChildren().addAll(boardTitle, scoreEntry);

            HBox btnBox = new HBox(16);
            btnBox.setAlignment(Pos.CENTER);

            Button playAgainBtn = new Button("Play Again");
            playAgainBtn.setFont(Font.font("Times New Roman", FontWeight.BOLD, 14));
            playAgainBtn.setStyle(
                    "-fx-background-color: " + ACCENT + ";" +
                            "-fx-text-fill: " + TEXT_PRI + ";" +
                            "-fx-background-radius: 2;" +
                            "-fx-padding: 9 30;" +
                            "-fx-cursor: hand;"
            );

            Button quitBtn = new Button("Quit");
            quitBtn.setFont(Font.font("Times New Roman", 14));
            quitBtn.setStyle(
                    "-fx-background-color: " + BG_RAISED + ";" +
                            "-fx-text-fill: " + TEXT_SEC + ";" +
                            "-fx-border-color: " + RULE + ";" +
                            "-fx-border-radius: 2;" +
                            "-fx-background-radius: 2;" +
                            "-fx-padding: 9 30;" +
                            "-fx-cursor: hand;"
            );

            playAgainBtn.setOnAction(ev -> showDifficultyScreen());
            quitBtn.setOnAction(ev -> showFinalGoodbye());

            btnBox.getChildren().addAll(playAgainBtn, quitBtn);
            root.getChildren().addAll(resultLabel, rule1, wordReveal, new Label(""), board, new Label(""), btnBox);
            primaryStage.setScene(new Scene(root, 520, 430));
        });
        pause.play();
    }

    // -------------------------------------------------------
    // Goodbye Screen
    // -------------------------------------------------------
    private void showFinalGoodbye() {
        VBox root = new VBox(18);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50, 60, 50, 60));
        root.setStyle("-fx-background-color: " + BG_DEEP + ";");

        Label bye = new Label("Thank you, " + player.getName() + ".");
        bye.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, 26));
        bye.setTextFill(Color.web(TEXT_PRI));

        Separator rule1 = new Separator();
        rule1.setMaxWidth(200);
        rule1.setStyle("-fx-background-color: " + RULE + ";");

        Label finalScore = new Label("Final score:   " + game.getScore() + " point(s)");
        finalScore.setFont(Font.font("Times New Roman", 18));
        finalScore.setTextFill(Color.web(ACCENT_LT));

        Button closeBtn = new Button("Close");
        closeBtn.setFont(Font.font("Times New Roman", 14));
        closeBtn.setStyle(
                "-fx-background-color: " + BG_RAISED + ";" +
                        "-fx-text-fill: " + TEXT_SEC + ";" +
                        "-fx-border-color: " + RULE + ";" +
                        "-fx-border-radius: 2;" +
                        "-fx-background-radius: 2;" +
                        "-fx-padding: 9 30;" +
                        "-fx-cursor: hand;"
        );
        closeBtn.setOnAction(e -> primaryStage.close());

        root.getChildren().addAll(bye, rule1, finalScore, new Label(""), closeBtn);
        primaryStage.setScene(new Scene(root, 460, 300));
    }

    // -------------------------------------------------------
    // getDisplayString() - char[] → spaced string
    // -------------------------------------------------------
    private String getDisplayString() {
        StringBuilder sb = new StringBuilder();
        for (char c : game.getDisplayWord()) {
            sb.append(c).append("   ");
        }
        return sb.toString().trim();
    }

    // -------------------------------------------------------
    // drawHangman() - renders on Canvas
    // Gallows in warm grey; figure body in indigo-violet
    // -------------------------------------------------------
    private void drawHangman(int stage) {
        GraphicsContext gc = hangmanCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, 200, 220);

        // Gallows - muted warm grey
        gc.setStroke(Color.web("#555555"));
        gc.setLineWidth(2.5);
        gc.strokeLine(20, 210, 180, 210); // base
        gc.strokeLine(60, 210, 60, 20);   // pole
        gc.strokeLine(60, 20, 130, 20);   // top bar
        gc.strokeLine(130, 20, 130, 50);  // rope

        // Figure - indigo-violet accent
        gc.setStroke(Color.web(ACCENT));
        gc.setLineWidth(2.5);

        if (stage >= 1) gc.strokeOval(110, 50, 40, 40);          // Head
        if (stage >= 2) gc.strokeLine(130, 90, 130, 150);        // Body
        if (stage >= 3) gc.strokeLine(130, 100, 100, 130);       // Left arm
        if (stage >= 4) gc.strokeLine(130, 100, 160, 130);       // Right arm
        if (stage >= 5) gc.strokeLine(130, 150, 100, 185);       // Left leg
        if (stage >= 6) gc.strokeLine(130, 150, 160, 185);       // Right leg
    }

    // -------------------------------------------------------
    // main()
    // -------------------------------------------------------
    public static void main(String[] args) {
        launch(args);
    }
}