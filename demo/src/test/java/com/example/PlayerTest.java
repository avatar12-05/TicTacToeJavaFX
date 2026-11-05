package com.example;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Scanner;
import org.junit.jupiter.api.Test;

/** Unit tests for human and CPU player move behavior. */
class PlayerTest {
    /** Verifies player names have the correct defaults and can be changed. */
    @Test
    void playersHaveDefaultNamesAndAllowNameUpdates() {
        HumanPlayer human = new HumanPlayer('X', new Scanner(""));
        CpuPlayer cpu = new CpuPlayer('O', new Random(7));

        assertEquals("Player #1", human.getName());
        assertEquals("CPU #1", cpu.getName());

        human.setName(" Alice ");
        cpu.setName(" Computer ");
        assertEquals(" Alice ", human.getName());
        assertEquals(" Computer ", cpu.getName());
    }

    /** Verifies null, empty, and whitespace-only player names are rejected. */
    @Test
    void rejectsNullOrBlankPlayerNames() {
        Player[] players = {
            new HumanPlayer('X', new Scanner("")),
            new CpuPlayer('O', new Random(7))
        };
        String[] defaultNames = {"Player #1", "CPU #1"};

        for (int index = 0; index < players.length; index++) {
            Player player = players[index];
            assertThrows(IllegalArgumentException.class, () -> player.setName(null));
            assertThrows(IllegalArgumentException.class, () -> player.setName(""));
            assertThrows(IllegalArgumentException.class, () -> player.setName(" \t\n "));
            assertEquals(defaultNames[index], player.getName());
        }
    }

    /** Verifies a human can place a marker at valid coordinates. */
    @Test
    void humanCanPlaceAMoveFromSelectedCoordinates() {
        Board board = new Board();
        HumanPlayer player = new HumanPlayer('X', new Scanner(""));

        assertTrue(player.makeMove(board, 2, 1));
        assertEquals('X', board.getCell(2, 1));
        assertFalse(player.makeMove(board, 2, 1));
        assertFalse(player.makeMove(board, -1, 0));
    }

    /** Verifies x is the column and y is the row after invalid input is retried. */
    @Test
    void humanRetriesMalformedAndOccupiedPositions() {
        Board board = new Board();
        board.place(0, 0, 'O');
        HumanPlayer player = new HumanPlayer('X', new Scanner("bad input\n1 1\n2 3\n"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            player.makeMove(board);
        } finally {
            System.setOut(originalOut);
        }
        assertEquals('X', board.getCell(2, 1));
        assertEquals(' ', board.getCell(1, 2));
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("enter x and y"));
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("invalid or occupied"));
    }

    /** Verifies the CPU chooses an empty cell and rejects a full board. */
    @Test
    void cpuSelectsAnEmptyCellAndCannotPlayOnAFullBoard() {
        Board board = new Board();
        board.place(0, 0, 'X');
        CpuPlayer cpu = new CpuPlayer('O', new Random(7));
        cpu.makeMove(board);
        assertEquals(1, countMarker(board, 'O'));
        assertEquals('X', board.getCell(0, 0));

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                if (board.isValid(row, column)) {
                    board.place(row, column, 'X');
                }
            }
        }
        assertThrows(IllegalStateException.class, () -> cpu.makeMove(board));
    }

    /** Counts the occurrences of a marker on the supplied board. */
    private int countMarker(Board board, char marker) {
        int count = 0;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                if (board.getCell(row, column) == marker) {
                    count++;
                }
            }
        }
        return count;
    }
}