# Tic-Tac-Toe Game

This is a simple console-based Tic-Tac-Toe game implemented in Java.

## How to Run

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/your-repo/your-project.git
    cd your-project
    ```
2.  **Run the application using Gradle:**
    ```bash
    ./gradlew run
    ```

## Game Description

The game is played on a 3x3 board. Two players, 'X' and 'O', take turns marking the spaces in the grid. The player who succeeds in placing three of their marks in a horizontal, vertical, or diagonal row wins the game.

## Project Structure

```
.
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── org/
│   │   │   │       └── example/
│   │   │   │           ├── Board.java   # Represents the game board
│   │   │   │           ├── Game.java    # Manages game logic and flow
│   │   │   │           ├── Main.java    # Main entry point for the application
│   │   │   │           └── Player.java  # Represents a player in the game
│   │   └── test/
│   │       └── java/
│   │           └── org/
│   │               └── example/
│   │                   └── BoardTest.java # Tests for the Board class
│   └── build.gradle.kts # Gradle build script for the application
├── gradlew               # Gradle wrapper script (Linux/macOS)
├── gradlew.bat           # Gradle wrapper script (Windows)
├── build.gradle.kts      # Main Gradle build script
└── README.md             # This file
```
