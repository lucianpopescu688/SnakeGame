package com.snakegame.dao;

import com.snakegame.model.Game;
import com.snakegame.model.GameMove;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameDAO {

    public int createGame(Game game) {
        String sql = "INSERT INTO games (user_id, score, time_spent, game_state, obstacles) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, game.getUserId());
            stmt.setInt(2, game.getScore());
            stmt.setLong(3, game.getTimeSpent());
            stmt.setString(4, game.getGameState());
            stmt.setString(5, game.getObstacles());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
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
        String sql = "UPDATE games SET score = ?, time_spent = ?, game_state = ?, ended_at = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, game.getScore());
            stmt.setLong(2, game.getTimeSpent());
            stmt.setString(3, game.getGameState());
            stmt.setTimestamp(4, game.getEndedAt());
            stmt.setInt(5, game.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Game getGameById(int id) {
        String sql = "SELECT * FROM games WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Game game = new Game();
                game.setId(rs.getInt("id"));
                game.setUserId(rs.getInt("user_id"));
                game.setScore(rs.getInt("score"));
                game.setTimeSpent(rs.getLong("time_spent"));
                game.setGameState(rs.getString("game_state"));
                game.setObstacles(rs.getString("obstacles"));
                game.setStartedAt(rs.getTimestamp("started_at"));
                game.setEndedAt(rs.getTimestamp("ended_at"));
                return game;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Game> getGamesByUserId(int userId) {
        List<Game> games = new ArrayList<>();
        String sql = "SELECT * FROM games WHERE user_id = ? ORDER BY started_at DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Game game = new Game();
                game.setId(rs.getInt("id"));
                game.setUserId(rs.getInt("user_id"));
                game.setScore(rs.getInt("score"));
                game.setTimeSpent(rs.getLong("time_spent"));
                game.setGameState(rs.getString("game_state"));
                game.setObstacles(rs.getString("obstacles"));
                game.setStartedAt(rs.getTimestamp("started_at"));
                game.setEndedAt(rs.getTimestamp("ended_at"));
                games.add(game);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return games;
    }

    public boolean addGameMove(GameMove move) {
        String sql = "INSERT INTO game_moves (game_id, move_direction, snake_position) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, move.getGameId());
            stmt.setString(2, move.getMoveDirection());
            stmt.setString(3, move.getSnakePosition());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<GameMove> getGameMoves(int gameId) {
        List<GameMove> moves = new ArrayList<>();
        String sql = "SELECT * FROM game_moves WHERE game_id = ? ORDER BY timestamp";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                GameMove move = new GameMove();
                move.setId(rs.getInt("id"));
                move.setGameId(rs.getInt("game_id"));
                move.setMoveDirection(rs.getString("move_direction"));
                move.setSnakePosition(rs.getString("snake_position"));
                move.setTimestamp(rs.getTimestamp("timestamp"));
                moves.add(move);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return moves;
    }
}