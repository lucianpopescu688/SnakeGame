package com.snakegame.servlet;

import com.snakegame.dao.GameDAO;
import com.snakegame.dao.GameMoveDAO;
import com.snakegame.model.Game;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@WebServlet("/game")
public class GameServlet extends HttpServlet {
    private GameDAO gameDAO;
    private GameMoveDAO gameMoveDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        gameDAO = new GameDAO();
        gameMoveDAO = new GameMoveDAO();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");
        String action = request.getParameter("action");

        if ("newGame".equals(action)) {
            // End any active game first
            Game activeGame = gameDAO.findActiveGameByUserId(userId);
            if (activeGame != null) {
                activeGame.setActive(false);
                activeGame.setEndTime(LocalDateTime.now());

                // Calculate total time
                long totalTime = ChronoUnit.SECONDS.between(activeGame.getStartTime(), activeGame.getEndTime());
                activeGame.setTotalTimeSeconds(totalTime);

                gameDAO.updateGame(activeGame);
            }

            // Create new game
            Game newGame = new Game(userId);
            int gameId = gameDAO.createGame(newGame);

            if (gameId > 0) {
                session.setAttribute("currentGameId", gameId);
                response.sendRedirect(request.getContextPath() + "/game");
            } else {
                request.setAttribute("error", "Failed to create new game");
                request.getRequestDispatcher("/game.jsp").forward(request, response);
            }
            return;
        }

        if ("endGame".equals(action)) {
            Integer gameId = (Integer) session.getAttribute("currentGameId");
            if (gameId != null) {
                Game game = gameDAO.findById(gameId);
                if (game != null && game.isActive()) {
                    game.setActive(false);
                    game.setEndTime(LocalDateTime.now());

                    // Calculate total time
                    long totalTime = ChronoUnit.SECONDS.between(game.getStartTime(), game.getEndTime());
                    game.setTotalTimeSeconds(totalTime);

                    gameDAO.updateGame(game);
                    session.removeAttribute("currentGameId");
                }
            }
            response.sendRedirect(request.getContextPath() + "/game");
            return;
        }

        if ("history".equals(action)) {
            List<Game> userGames = gameDAO.getGamesByUserId(userId);
            request.setAttribute("games", userGames);
            request.getRequestDispatcher("/game-history.jsp").forward(request, response);
            return;
        }

        if ("obstacles".equals(action)) {
            // Return obstacles as JSON
            List<int[]> obstacles = gameMoveDAO.getObstacles();
            JsonArray obstaclesJson = new JsonArray();

            for (int[] obstacle : obstacles) {
                JsonObject obstacleObj = new JsonObject();
                obstacleObj.addProperty("x", obstacle[0]);
                obstacleObj.addProperty("y", obstacle[1]);
                obstaclesJson.add(obstacleObj);
            }

            response.setContentType("application/json");
            response.getWriter().write(obstaclesJson.toString());
            return;
        }

        // Default: show game page
        Game currentGame = null;
        Integer currentGameId = (Integer) session.getAttribute("currentGameId");

        if (currentGameId != null) {
            currentGame = gameDAO.findById(currentGameId);
            if (currentGame == null || !currentGame.isActive()) {
                session.removeAttribute("currentGameId");
                currentGame = null;
            }
        }

        // If no active game, find the most recent active game
        if (currentGame == null) {
            currentGame = gameDAO.findActiveGameByUserId(userId);
            if (currentGame != null) {
                session.setAttribute("currentGameId", currentGame.getId());
            }
        }

        request.setAttribute("currentGame", currentGame);
        request.getRequestDispatcher("/game.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");
        String action = request.getParameter("action");

        if ("updateScore".equals(action)) {
            Integer gameId = (Integer) session.getAttribute("currentGameId");
            String scoreStr = request.getParameter("score");

            if (gameId != null && scoreStr != null) {
                try {
                    int score = Integer.parseInt(scoreStr);
                    Game game = gameDAO.findById(gameId);

                    if (game != null && game.isActive() && game.getUserId() == userId) {
                        game.setScore(score);

                        // Update total time
                        long totalTime = ChronoUnit.SECONDS.between(game.getStartTime(), LocalDateTime.now());
                        game.setTotalTimeSeconds(totalTime);

                        gameDAO.updateGame(game);

                        response.setContentType("application/json");
                        JsonObject result = new JsonObject();
                        result.addProperty("success", true);
                        response.getWriter().write(result.toString());
                        return;
                    }
                } catch (NumberFormatException e) {
                    // Invalid score format
                }
            }

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject error = new JsonObject();
            error.addProperty("success", false);
            error.addProperty("error", "Invalid request");
            response.getWriter().write(error.toString());
        }
    }
}