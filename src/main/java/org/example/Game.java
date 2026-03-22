package org.example;

import java.util.Scanner;

public class Game {
    private final Board board;
    private final Player player1;
    private final Player player2;
    private Player currentPlayer;

    public Game() {
        board = new Board();
        player1 = new Player('X');
        player2 = new Player('O');
        currentPlayer = player1;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            board.print();
            System.out.println("Player " + currentPlayer.getSymbol() + ", enter your move (row and column): ");
            int row = scanner.nextInt() - 1;
            int col = scanner.nextInt() - 1;

            if (row >= 0 && row < 3 && col >= 0 && col < 3 && board.isCellEmpty(row, col)) {
                board.place(row, col, currentPlayer.getSymbol());
                if (board.hasWinner()) {
                    board.print();
                    System.out.println("Player " + currentPlayer.getSymbol() + " wins!");
                    break;
                }
                if (board.isFull()) {
                    board.print();
                    System.out.println("It's a draw!");
                    break;
                }
                currentPlayer = (currentPlayer == player1) ? player2 : player1;
            } else {
                System.out.println("Invalid move. Try again.");
            }
        }
        scanner.close();
    }
}
