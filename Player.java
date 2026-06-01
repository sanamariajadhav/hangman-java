// ============================================================
// Keeps track of the player's name and which words they've already played.
// This ensures no word repeats for the same player in one session.
// ENCAPSULATION (private fields + getters/setters)
// ============================================================

import java.util.HashSet; // HashSet is like a list that doesn't allow duplicates
import java.util.Set;

public class Player
{
    private String name;
    private Set<String> playedWords; // stores words this player has already seen
    public Player(String name)
    {
        this.name = name;
        this.playedWords = new HashSet<>(); // empty set of played words for new player
    }
    // Add a word to this player's played list
    public void addPlayedWord(String word)
    {
        playedWords.add(word);
    }
    // Check if this player has already played a specific word
    public boolean hasPlayedWord(String word)
    {
        return playedWords.contains(word);
    }
    // Getter for name
    public String getName()
    {
        return name;
    }
    // Getter for played words (in case we need to see the full set)
    public Set<String> getPlayedWords()
    {
        return playedWords;
    }
}
