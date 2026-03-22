package org.example;

public class Board {
    private final char[][] cells;
    private static final int SIZE = 3;

    public Board() {
        cells = new char[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                cells[i][j] = '-';
            }
        }
    }

    public void print() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                System.out.print(cells[i][j] + " ");
            }
            System.out.println();
        }
    }

    public boolean isCellEmpty(int row, int col) {
        return cells[row][col] == '-';
    }

    public char getCell(int row, int col) {
        return cells[row][col];
    }

    public boolean place(int row, int col, char symbol) {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
            return false; // Out of bounds
        }
        if (cells[row][col] != '-') {
            return false; // Cell already occupied
        }
        cells[row][col] = symbol;
        return true;
    }

    public boolean isFull() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (cells[i][j] == '-') {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean hasWinner() {
        // Check rows
        for (int i = 0; i < SIZE; i++) {
            if (cells[i][0] != '-' && cells[i][0] == cells[i][1] && cells[i][1] == cells[i][2]) {
                return true;
            }
        }
        // Check columns
        for (int j = 0; j < SIZE; j++) {
            if (cells[0][j] != '-' && cells[0][j] == cells[1][j] && cells[1][j] == cells[2][j]) {
                return true;
            }
        }
        // Check diagonals
        if (cells[0][0] != '-' && cells[0][0] == cells[1][1] && cells[1][1] == cells[2][2]) {
            return true;
        }
        if (cells[0][2] != '-' && cells[0][2] == cells[1][1] && cells[1][1] == cells[2][0]) {
            return true;
        }
        return false;
    }
}
