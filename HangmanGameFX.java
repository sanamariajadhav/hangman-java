// ============================================================
// Game logic class for the JavaFX
// EXTENDS Game -Inheritance, OVERRIDES play() -Polymorphism.
// This class manages game STATE -current word, guesses, etc.
// ============================================================

import java.util.ArrayList;

public class HangmanGameFX extends Game
{

    //Encapsulation
    private String wordToGuess;
    private char[] displayWord;
    private ArrayList<Character> guessedLetters;
    private int wrongGuessesLeft;
    private Player player;
    private WordLoader wordLoader;
    private static final char[] VOWELS = {'a', 'e', 'i', 'o', 'u'};
    public  static final int MAX_WRONG_GUESSES = 6;
    public HangmanGameFX(Player player, WordLoader wordLoader)
    {
        super(player.getName());
        this.player = player;
        this.wordLoader = wordLoader;
    }
    public boolean startNewRound(String difficulty)
    {
        try
        {
            wordToGuess = wordLoader.getRandomWord(difficulty, player);
            if (wordToGuess == null) return false;
            player.addPlayedWord(wordToGuess);
            wrongGuessesLeft = MAX_WRONG_GUESSES;
            guessedLetters = new ArrayList<>();
            displayWord = new char[wordToGuess.length()];
            for (int i = 0; i < wordToGuess.length(); i++)
            {
                displayWord[i] = isVowel(wordToGuess.charAt(i)) ? '*' : '_';
            }
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
    public String guessLetter(char letter)
    {
        if (guessedLetters.contains(letter))
        {
            return "already";
        }
        guessedLetters.add(letter);
        if (wordToGuess.contains(String.valueOf(letter)))
        {
            for (int i = 0; i < wordToGuess.length(); i++)
            {
                if (wordToGuess.charAt(i) == letter)
                {
                    displayWord[i] = letter;
                }
            }
            if (isWordComplete())
            {
                incrementScore();
                return "won";
            }
            return "correct";
        }
        else
        {
            wrongGuessesLeft--;
            if (wrongGuessesLeft == 0)
            {
                return "lost";
            }
            return "wrong";
        }
    }
    private boolean isVowel(char c)
    {
        for (char v : VOWELS)
        {
            if (c == v) return true;
        }
        return false;
    }
    private boolean isWordComplete()
    {
        for (char c : displayWord)
        {
            if (c == '_' || c == '*') return false;
        }
        return true;
    }
    //For UI to update the display
    public char[] getDisplayWord()
    {
        return displayWord;
    }
    public int getWrongGuessesLeft()
    {
        return wrongGuessesLeft;
    }
    public ArrayList<Character> getGuessedLetters()
    {
        return guessedLetters;
    }
    public String getWordToGuess()
    {
        return wordToGuess;
    }
    public Player getPlayer()
    {
        return player;
    }
}
