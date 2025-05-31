package com.snakegame.dao;

import com.snakegame.model.GameMove;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GameMoveDAO {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public boolean createGameMove(GameMove gameMove) {
        String sql = "INSERT INTO game_moves (game_id, move_number, direction, snake_positions, timestamp) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, gameMove.getGameId());
            pstmt.setInt(2, gameMove.getMoveNumber());
            pstmt.setString(3, gameMove.getDirection());
            pstmt.setString(4, gameMove.getSnakePositions());
            pstmt.setString(5, gameMove.getTimestamp().format(FORMATTER));

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<GameMove> getMovesByGameId(int gameId) {
        List<GameMove> moves = new ArrayList<>();
        String sql = "SELECT * FROM game_moves WHERE game_id = ? ORDER BY move_number ASC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, gameId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                moves.add(new GameMove(
                        rs.getInt("id"),
                        rs.getInt("game_id"),
                        rs.getInt("move_number"),
                        rs.getString("direction"),
                        rs.getString("snake_positions"),
                        LocalDateTime.parse(rs.getString("timestamp"), FORMATTER)
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return moves;
    }

    public GameMove getLastMoveByGameId(int gameId) {
        String sql = "SELECT * FROM game_moves WHERE game_id = ? ORDER BY move_number DESC LIMIT 1";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, gameId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new GameMove(
                        rs.getInt("id"),
                        rs.getInt("game_id"),
                        rs.getInt("move_number"),
                        rs.getString("direction"),
                        rs.getString("snake_positions"),
                        LocalDateTime.parse(rs.getString("timestamp"), FORMATTER)
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getMoveCountByGameId(int gameId) {
        String sql = "SELECT COUNT(*) FROM game_moves WHERE game_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, gameId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<int[]> getObstacles() {
        List<int[]> obstacles = new ArrayList<>();
        String sql = "SELECT x, y FROM obstacles WHERE is_active = 1";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                obstacles.add(new int[]{rs.getInt("x"), rs.getInt("y")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return obstacles;
    }
}