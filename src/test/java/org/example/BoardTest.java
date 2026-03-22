package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {
    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    void testInitialBoardIsEmpty() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals('-', board.getCell(i, j));
            }
        }
    }

    @Test
    void testPlaceMove() {
        assertTrue(board.place(0, 0, 'X'));
        assertEquals('X', board.getCell(0, 0));
    }

    @Test
    void testPlaceMoveOnOccupiedCell() {
        board.place(0, 0, 'X');
        assertFalse(board.place(0, 0, 'O')); // Should return false as cell is occupied
        assertEquals('X', board.getCell(0, 0)); // Should remain 'X'
    }

    @Test
    void testPlaceMoveOutOfBounds() {
        assertFalse(board.place(-1, 0, 'X'));
        assertFalse(board.place(3, 0, 'X'));
        assertFalse(board.place(0, -1, 'X'));
        assertFalse(board.place(0, 3, 'X'));
        assertEquals('-', board.getCell(0,0)); // Ensure no change to valid cells
    }

    @Test
    void testHasWinnerRow() {
        board.place(0, 0, 'X');
        board.place(0, 1, 'X');
        board.place(0, 2, 'X');
        assertTrue(board.hasWinner());
    }

    @Test
    void testHasWinnerColumn() {
        board.place(0, 0, 'O');
        board.place(1, 0, 'O');
        board.place(2, 0, 'O');
        assertTrue(board.hasWinner());
    }

    @Test
    void testHasWinnerDiagonalLeftToRight() {
        board.place(0, 0, 'X');
        board.place(1, 1, 'X');
        board.place(2, 2, 'X');
        assertTrue(board.hasWinner());
    }

    @Test
    void testHasWinnerDiagonalRightToLeft() {
        board.place(0, 2, 'O');
        board.place(1, 1, 'O');
        board.place(2, 0, 'O');
        assertTrue(board.hasWinner());
    }

    @Test
    void testNoWinner() {
        board.place(0, 0, 'X');
        board.place(0, 1, 'O');
        board.place(0, 2, 'X');
        board.place(1, 0, 'O');
        board.place(1, 1, 'X');
        board.place(1, 2, 'O');
        assertFalse(board.hasWinner());
    }

    @Test
    void testIsBoardFullDraw() {
        board.place(0, 0, 'X'); board.place(0, 1, 'O'); board.place(0, 2, 'X');
        board.place(1, 0, 'X'); board.place(1, 1, 'O'); board.place(1, 2, 'O');
        board.place(2, 0, 'O'); board.place(2, 1, 'X'); board.place(2, 2, 'X');
        assertTrue(board.isFull());
        assertFalse(board.hasWinner());
    }

    @Test
    void testIsBoardNotFull() {
        board.place(0, 0, 'X');
        assertFalse(board.isFull());
    }
}