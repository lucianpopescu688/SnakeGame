package com.snakegame.model;

import java.time.LocalDateTime;

public class Game {
    private int id;
    private int userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int score;
    private boolean isActive;
    private long totalTimeSeconds;

    // Constructors
    public Game() {}

    public Game(int userId) {
        this.userId = userId;
        this.startTime = LocalDateTime.now();
        this.score = 0;
        this.isActive = true;
        this.totalTimeSeconds = 0;
    }

    public Game(int id, int userId, LocalDateTime startTime, LocalDateTime endTime,
                int score, boolean isActive, long totalTimeSeconds) {
        this.id = id;
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.score = score;
        this.isActive = isActive;
        this.totalTimeSeconds = totalTimeSeconds;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public long getTotalTimeSeconds() {
        return totalTimeSeconds;
    }

    public void setTotalTimeSeconds(long totalTimeSeconds) {
        this.totalTimeSeconds = totalTimeSeconds;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", userId=" + userId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", score=" + score +
                ", isActive=" + isActive +
                ", totalTimeSeconds=" + totalTimeSeconds +
                '}';
    }
}