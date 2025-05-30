package com.snakegame.dao;

import com.snakegame.model.Game;
import com.snakegame.model.GameMove;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameDAO {

    public int createGame(Game game) {
        String sql = "INSERT INTO games (user_id, score, time_spent, game_state, obstacles, started_at) VALUES (?, ?, ?, ?, ?, ?)";

        System.out.println("GameDAO.createGame called");
        System.out.println("Game user ID: " + game.getUserId());
        System.out.println("Game obstacles: " + game.getObstacles());

        try (Connection conn = DatabaseUtil.getConnection()) {
            System.out.println("Database connection obtained successfully");

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                System.out.println("PreparedStatement created");

                // Bind parameters in the correct order
                stmt.setInt(1, game.getUserId());                     // user_id
                stmt.setInt(2, 0);                                    // score = 0 at start
                stmt.setLong(3, 0L);                                  // time_spent = 0
                stmt.setString(4, "{}");                              // empty initial game_state
                stmt.setString(5, game.getObstacles());               // obstacles JSON
                stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis())); // started_at

                System.out.println("Parameters bound successfully");

                int rows = stmt.executeUpdate();
                System.out.println("Rows affected: " + rows);

                if (rows == 0) {
                    System.out.println("No rows were inserted");
                    return -1;   // insertion failed
                }

                // Get the generated key
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int gameId = generatedKeys.getInt(1);
                        System.out.println("Generated game ID: " + gameId);
                        return gameId;
                    } else {
                        System.out.println("No ID was generated");
                        return -1;
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("SQLException in GameDAO.createGame:");
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            return -1;
        } catch (Exception e) {
            System.err.println("Unexpected exception in GameDAO.createGame:");
            e.printStackTrace();
            return -1;
        }
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
            System.err.println("SQLException in GameDAO.updateGame:");
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
            System.err.println("SQLException in GameDAO.getGameById:");
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
            System.err.println("SQLException in GameDAO.getGamesByUserId:");
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
            System.err.println("SQLException in GameDAO.addGameMove:");
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
            System.err.println("SQLException in GameDAO.getGameMoves:");
            e.printStackTrace();
        }

        return moves;
    }
}