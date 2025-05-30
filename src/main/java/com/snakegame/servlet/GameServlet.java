package com.snakegame.servlet;

import com.snakegame.dao.GameDAO;
import com.snakegame.model.Game;
import com.snakegame.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Random;

@WebServlet("/GameServlet")
public class GameServlet extends HttpServlet {

    private GameDAO gameDAO;

    @Override
    public void init() throws ServletException {
        gameDAO = new GameDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        if ("startGame".equals(action)) {
            // Generate random obstacles
            String obstacles = generateObstacles();

            Game game = new Game(user.getId(), obstacles);
            int gameId = gameDAO.createGame(game);

            if (gameId > 0) {
                session.setAttribute("currentGameId", gameId);
                session.setAttribute("gameStartTime", System.currentTimeMillis());
                out.print("{\"success\": true, \"gameId\": " + gameId + ", \"obstacles\": \"" + obstacles + "\"}");
            } else {
                out.print("{\"success\": false, \"message\": \"Failed to start game\"}");
            }

        } else if ("endGame".equals(action)) {
            Integer gameId = (Integer) session.getAttribute("currentGameId");
            Long startTime = (Long) session.getAttribute("gameStartTime");

            if (gameId != null && startTime != null) {
                int score = Integer.parseInt(request.getParameter("score"));
                String gameState = request.getParameter("gameState");

                long timeSpent = (System.currentTimeMillis() - startTime) / 1000; // Convert to seconds

                Game game = gameDAO.getGameById(gameId);
                if (game != null) {
                    game.setScore(score);
                    game.setTimeSpent(timeSpent);
                    game.setGameState(gameState);
                    game.setEndedAt(new Timestamp(System.currentTimeMillis()));

                    boolean updated = gameDAO.updateGame(game);
                    if (updated) {
                        out.print("{\"success\": true, \"timeSpent\": " + timeSpent + "}");
                    } else {
                        out.print("{\"success\": false, \"message\": \"Failed to save game\"}");
                    }
                } else {
                    out.print("{\"success\": false, \"message\": \"Game not found\"}");
                }

                // Clear session data
                session.removeAttribute("currentGameId");
                session.removeAttribute("gameStartTime");
            } else {
                out.print("{\"success\": false, \"message\": \"No active game found\"}");
            }

        } else if ("updateGame".equals(action)) {
            Integer gameId = (Integer) session.getAttribute("currentGameId");

            if (gameId != null) {
                int score = Integer.parseInt(request.getParameter("score"));
                String gameState = request.getParameter("gameState");

                Game game = gameDAO.getGameById(gameId);
                if (game != null) {
                    game.setScore(score);
                    game.setGameState(gameState);

                    boolean updated = gameDAO.updateGame(game);
                    out.print("{\"success\": " + updated + "}");
                } else {
                    out.print("{\"success\": false, \"message\": \"Game not found\"}");
                }
            } else {
                out.print("{\"success\": false, \"message\": \"No active game\"}");
            }
        }

        out.flush();
    }

    private String generateObstacles() {
        Random random = new Random();
        StringBuilder obstacles = new StringBuilder("[");

        // Generate 8-12 random obstacles on a 20x20 grid
        int numObstacles = 8 + random.nextInt(5);

        for (int i = 0; i < numObstacles; i++) {
            if (i > 0) obstacles.append(",");

            int x = 2 + random.nextInt(16); // Avoid edges
            int y = 2 + random.nextInt(16);

            obstacles.append("{\"x\":").append(x).append(",\"y\":").append(y).append("}");
        }

        obstacles.append("]");
        return obstacles.toString();
    }
}