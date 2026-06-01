// ============================================================
// ABSTRACT class
// - ABSTRACTION and INHERITANCE
// ============================================================

public abstract class Game
{
    // Encapsulation- private fields, accessed via getters/setters  etc
    private String playerName;
    private int score;
    public Game(String playerName)
    {
        this.playerName = playerName;
        this.score = 0;
    }
    // Getters and Setters- Encapsulation
    public String getPlayerName()
    {
        return playerName;
    }
    public int getScore()
    {
        return score;
    }
    public void setScore(int score)
    {
        this.score = score;
    }
    public void incrementScore()
    {
        this.score++;
    }
}
