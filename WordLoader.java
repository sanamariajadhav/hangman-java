// ============================================================
// Picks a random word directly from words.txt
// Two-Pass Method-
//   Pass 1: Count how many words in the file match the difficulty level
//   Pass 2: Go through the file again, stop at a random matching word
// FILE HANDLING and EXCEPTION HANDLING
// ============================================================

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
public class WordLoader
{
    private String filePath;
    public WordLoader(String filePath)
    {
        this.filePath = filePath;
    }
    public String getRandomWord(String difficulty, Player player) throws IOException
    {
        String selectedWord = null;
        int maxAttempts = 50; // tries to not repeat word
        int attempts = 0;
        while (attempts < maxAttempts)
        {
            // PASS 1
            int matchingCount = 0;
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null)
            {
                line = line.trim().toLowerCase();
                if (line.isEmpty()) continue;
                if (matchesDifficulty(line, difficulty))
                {
                    matchingCount++;
                }
            }
            reader.close();
            // If no words found for level, throw an error
            if (matchingCount == 0)
            {
                throw new IOException("No words found for difficulty: " + difficulty);
            }
            // Random number between 1 and matchingCount
            Random random = new Random();
            int targetIndex = random.nextInt(matchingCount) + 1;
            // PASS 2
            reader = new BufferedReader(new FileReader(filePath));
            int currentIndex = 0;
            while ((line = reader.readLine()) != null)
            {
                line = line.trim().toLowerCase();
                if (line.isEmpty()) continue;
                if (matchesDifficulty(line, difficulty))
                {
                    currentIndex++;
                    if (currentIndex == targetIndex)
                    {
                        selectedWord = line;
                        break;
                    }
                }
            }
            reader.close();
            // Check if this player already played this word
            if (selectedWord != null && !player.hasPlayedWord(selectedWord))
            {
                break; // fresh word found
            }
            attempts++;
        }
        return selectedWord; // still returns a word if no fresh word is found
    }
    private boolean matchesDifficulty(String word, String difficulty)
    {
        int len = word.length();
        if (difficulty.equals("easy"))   return len >= 4 && len <= 5;
        if (difficulty.equals("medium")) return len >= 6 && len <= 7;
        if (difficulty.equals("hard"))   return len >= 8;
        return false;
    }
}
