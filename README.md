<h1 align="center">Hangman — Word Guessing Game</h1>

<p align="center">
  <code>Java</code> &nbsp;&nbsp; <code>JavaFX</code> &nbsp;&nbsp; <code>Object-Oriented Programming</code>
</p>

<p align="center">
  Academic Project &nbsp;·&nbsp; MPSTME, NMIMS &nbsp;·&nbsp; 2025–2026
</p>

---

### Overview

A fully functional, GUI-based digital implementation of the classic Hangman game, built using Java and JavaFX. The application allows a player to guess a hidden word letter by letter within a limited number of attempts, with the hangman figure drawn progressively on incorrect guesses.

The project was developed as part of the Object-Oriented Programming course and demonstrates the practical application of core OOP principles — Encapsulation, Inheritance, Polymorphism, and Abstraction — in building a structured, modular desktop application.

---

### Features

- Interactive multi-screen GUI (Welcome, Difficulty Selection, Gameplay, Results)
- Three difficulty levels — Easy (4–5 letters), Medium (6–7 letters), Hard (8+ letters)
- Dynamic word loading from an external file using a two-pass random selection algorithm
- Vowel hint system — vowels pre-revealed as `*` at game start
- Real-time hangman figure rendering on incorrect guesses
- Duplicate word prevention per session using `HashSet`
- Score tracking and leaderboard display

---

### Class Structure

<table style="border: none; border-collapse: collapse;">
  <tr>
    <td style="border: none; padding: 6px 24px 6px 0;"><strong>Game.java</strong></td>
    <td style="border: none; padding: 6px 0;">Abstract base class — holds player name and score; defines shared game behaviour</td>
  </tr>
  <tr>
    <td style="border: none; padding: 6px 24px 6px 0;"><strong>HangmanGameFX.java</strong></td>
    <td style="border: none; padding: 6px 0;">Extends <code>Game</code> — manages core game logic: word selection, letter guessing, win/loss conditions</td>
  </tr>
  <tr>
    <td style="border: none; padding: 6px 24px 6px 0;"><strong>HangmanFX.java</strong></td>
    <td style="border: none; padding: 6px 0;">Main JavaFX application class — handles all UI screens and event-driven interactions</td>
  </tr>
  <tr>
    <td style="border: none; padding: 6px 24px 6px 0;"><strong>Player.java</strong></td>
    <td style="border: none; padding: 6px 0;">Stores player name and played word history to prevent repetition</td>
  </tr>
  <tr>
    <td style="border: none; padding: 6px 24px 6px 0;"><strong>WordLoader.java</strong></td>
    <td style="border: none; padding: 6px 0;">Reads from <code>words.txt</code> and selects a random word matching the chosen difficulty via a two-pass method</td>
  </tr>
</table>

---

### OOP Concepts Applied

<table style="border: none; border-collapse: collapse;">
  <tr>
    <td style="border: none; padding: 6px 24px 6px 0;"><strong>Encapsulation</strong></td>
    <td style="border: none; padding: 6px 0;">All class fields are private, accessed via getters and setters</td>
  </tr>
  <tr>
    <td style="border: none; padding: 6px 24px 6px 0;"><strong>Inheritance</strong></td>
    <td style="border: none; padding: 6px 0;"><code>HangmanGameFX</code> extends the abstract <code>Game</code> class</td>
  </tr>
  <tr>
    <td style="border: none; padding: 6px 24px 6px 0;"><strong>Polymorphism</strong></td>
    <td style="border: none; padding: 6px 0;">Overridden methods adapt base class behaviour for the Hangman context</td>
  </tr>
  <tr>
    <td style="border: none; padding: 6px 24px 6px 0;"><strong>Abstraction</strong></td>
    <td style="border: none; padding: 6px 0;">Complex game logic is hidden behind simple user-facing actions</td>
  </tr>
</table>

---

### Technologies

<table style="border: none; border-collapse: collapse;">
  <tr>
    <td style="border: none; padding: 6px 24px 6px 0;"><strong>Language</strong></td>
    <td style="border: none; padding: 6px 0;">Java</td>
  </tr>
  <tr>
    <td style="border: none; padding: 6px 24px 6px 0;"><strong>UI Framework</strong></td>
    <td style="border: none; padding: 6px 0;">JavaFX</td>
  </tr>
  <tr>
    <td style="border: none; padding: 6px 24px 6px 0;"><strong>Libraries</strong></td>
    <td style="border: none; padding: 6px 0;">Java IO (BufferedReader, FileReader) · Java Collections (ArrayList, HashSet)</td>
  </tr>
</table>

---

### How to Run

1. Clone the repository
2. Ensure Java and JavaFX are installed
3. Compile all `.java` files
4. Run `HangmanFX.java` as the main class
5. Ensure `words.txt` is in the same directory as the compiled files

---

*Academic project submitted in partial fulfilment of B.Tech in Information Technology, MPSTME NMIMS, 2025–2026.*
