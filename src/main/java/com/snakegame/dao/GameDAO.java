package com.snakegame.dao;

import com.snakegame.model.Game;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GameDAO {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public int createGame(Game game) {
        String sql = "INSERT INTO games (user_id, start_time, score, is_active, total_time_seconds) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, game.getUserId());
            pstmt.setString(2, game.getStartTime().format(FORMATTER));
            pstmt.setInt(3, game.getScore());
            pstmt.setBoolean(4, game.isActive());
            pstmt.setLong(5, game.getTotalTimeSeconds());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean updateGame(Game game) {
        String sql = "UPDATE games SET end_time = ?, score = ?, is_active = ?, total_time_seconds = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, game.getEndTime() != null ? game.getEndTime().format(FORMATTER) : null);
            pstmt.setInt(2, game.getScore());
            pstmt.setBoolean(3, game.isActive());
            pstmt.setLong(4, game.getTotalTimeSeconds());
            pstmt.setInt(5, game.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Game findById(int id) {
        String sql = "SELECT * FROM games WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                LocalDateTime endTime = null;
                String endTimeStr = rs.getString("end_time");
                if (endTimeStr != null) {
                    endTime = LocalDateTime.parse(endTimeStr, FORMATTER);
                }

                return new Game(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        LocalDateTime.parse(rs.getString("start_time"), FORMATTER),
                        endTime,
                        rs.getInt("score"),
                        rs.getBoolean("is_active"),
                        rs.getLong("total_time_seconds")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Game findActiveGameByUserId(int userId) {
        String sql = "SELECT * FROM games WHERE user_id = ? AND is_active = 1 ORDER BY start_time DESC LIMIT 1";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                LocalDateTime endTime = null;
                String endTimeStr = rs.getString("end_time");
                if (endTimeStr != null) {
                    endTime = LocalDateTime.parse(endTimeStr, FORMATTER);
                }

                return new Game(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        LocalDateTime.parse(rs.getString("start_time"), FORMATTER),
                        endTime,
                        rs.getInt("score"),
                        rs.getBoolean("is_active"),
                        rs.getLong("total_time_seconds")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Game> getGamesByUserId(int userId) {
        List<Game> games = new ArrayList<>();
        String sql = "SELECT * FROM games WHERE user_id = ? ORDER BY start_time DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                LocalDateTime endTime = null;
                String endTimeStr = rs.getString("end_time");
                if (endTimeStr != null) {
                    endTime = LocalDateTime.parse(endTimeStr, FORMATTER);
                }

                games.add(new Game(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        LocalDateTime.parse(rs.getString("start_time"), FORMATTER),
                        endTime,
                        rs.getInt("score"),
                        rs.getBoolean("is_active"),
                        rs.getLong("total_time_seconds")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return games;
    }

    public List<Game> getTopScores(int limit) {
        List<Game> games = new ArrayList<>();
        String sql = "SELECT * FROM games WHERE is_active = 0 ORDER BY score DESC LIMIT ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                LocalDateTime endTime = null;
                String endTimeStr = rs.getString("end_time");
                if (endTimeStr != null) {
                    endTime = LocalDateTime.parse(endTimeStr, FORMATTER);
                }

                games.add(new Game(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        LocalDateTime.parse(rs.getString("start_time"), FORMATTER),
                        endTime,
                        rs.getInt("score"),
                        rs.getBoolean("is_active"),
                        rs.getLong("total_time_seconds")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return games;
    }
}