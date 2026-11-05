package com.example;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * Defines the shared marker and move contract for a Tic-Tac-Toe player.
 */ 
public abstract class Player {
    private static final AtomicInteger HUMAN_NAME_SEQUENCE = new AtomicInteger();
    private static final AtomicInteger CPU_NAME_SEQUENCE = new AtomicInteger();

    protected final char marker;
    private String name;

    /**
     * Creates a player with the given marker.
     *
     * @param marker the player's marker, either {@code X} or {@code O}
     * @throws IllegalArgumentException if the marker is not {@code X} or {@code O}
     */
    protected Player(char marker) {
        if (marker != 'X' && marker != 'O') {
            throw new IllegalArgumentException("A player marker must be X or O");
        }
        this.marker = marker;
    }

    static String nextHumanName() {
        return "Player #" + HUMAN_NAME_SEQUENCE.incrementAndGet();
    }

    static String nextCpuName() {
        return "CPU #" + CPU_NAME_SEQUENCE.incrementAndGet();
    }

    static void resetNameSequences() {
        HUMAN_NAME_SEQUENCE.set(0);
        CPU_NAME_SEQUENCE.set(0);
    }

    /**
     * Returns this player's name.
     *
     * @return this player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets this player's name.
     *
     * @param name the new player name
     * @throws IllegalArgumentException if {@code name} is null or blank
     */
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("A player name must not be null or blank");
        }
        this.name = name;
    }

    /**
     * Returns this player's marker.
     *
     * @return the player's marker
     */
    public char getMarker() {
        return marker;
    }

    /**
     * Makes a move on the supplied board.
     *
     * @param board board on which to make the move
     */
    public abstract void makeMove(Board board);
}