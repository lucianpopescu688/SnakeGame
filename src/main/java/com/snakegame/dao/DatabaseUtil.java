package com.snakegame.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtil {
    private static final String DB_PATH = "snake_game.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Create users table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    created_at TEXT NOT NULL
                )
            """);

            // Create games table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS games (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    start_time TEXT NOT NULL,
                    end_time TEXT,
                    score INTEGER DEFAULT 0,
                    is_active INTEGER DEFAULT 1,
                    total_time_seconds INTEGER DEFAULT 0,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """);

            // Create game_moves table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS game_moves (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    game_id INTEGER NOT NULL,
                    move_number INTEGER NOT NULL,
                    direction TEXT NOT NULL,
                    snake_positions TEXT NOT NULL,
                    timestamp TEXT NOT NULL,
                    FOREIGN KEY (game_id) REFERENCES games(id)
                )
            """);

            // Create obstacles table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS obstacles (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    x INTEGER NOT NULL,
                    y INTEGER NOT NULL,
                    is_active INTEGER DEFAULT 1
                )
            """);

            // Insert default obstacles if table is empty
            stmt.execute("""
                INSERT OR IGNORE INTO obstacles (x, y) VALUES 
                (5, 5), (10, 8), (15, 12), (3, 15), (18, 3),
                (12, 6), (7, 18), (20, 10), (14, 14), (2, 9)
            """);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}