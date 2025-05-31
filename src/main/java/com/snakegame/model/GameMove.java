package com.snakegame.model;

import java.time.LocalDateTime;

public class GameMove {
    private int id;
    private int gameId;
    private int moveNumber;
    private String direction;
    private String snakePositions; // JSON string representing snake positions
    private LocalDateTime timestamp;

    // Constructors
    public GameMove() {}

    public GameMove(int gameId, int moveNumber, String direction, String snakePositions) {
        this.gameId = gameId;
        this.moveNumber = moveNumber;
        this.direction = direction;
        this.snakePositions = snakePositions;
        this.timestamp = LocalDateTime.now();
    }

    public GameMove(int id, int gameId, int moveNumber, String direction,
                    String snakePositions, LocalDateTime timestamp) {
        this.id = id;
        this.gameId = gameId;
        this.moveNumber = moveNumber;
        this.direction = direction;
        this.snakePositions = snakePositions;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public void setMoveNumber(int moveNumber) {
        this.moveNumber = moveNumber;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getSnakePositions() {
        return snakePositions;
    }

    public void setSnakePositions(String snakePositions) {
        this.snakePositions = snakePositions;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "GameMove{" +
                "id=" + id +
                ", gameId=" + gameId +
                ", moveNumber=" + moveNumber +
                ", direction='" + direction + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}