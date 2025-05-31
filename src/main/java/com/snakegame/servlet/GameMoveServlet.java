package com.snakegame.servlet;

import com.snakegame.dao.GameDAO;
import com.snakegame.dao.GameMoveDAO;
import com.snakegame.model.Game;
import com.snakegame.model.GameMove;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/gameMove")
public class GameMoveServlet extends HttpServlet {
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");
        Integer gameId = (Integer) session.getAttribute("currentGameId");

        String direction = request.getParameter("direction");
        String snakePositions = request.getParameter("snakePositions");

        // Validation
        if (gameId == null || direction == null || snakePositions == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Verify game belongs to user and is active
        Game game = gameDAO.findById(gameId);
        if (game == null || game.getUserId() != userId || !game.isActive()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Get next move number
        int moveCount = gameMoveDAO.getMoveCountByGameId(gameId);
        int nextMoveNumber = moveCount + 1;

        // Create and save the move
        GameMove gameMove = new GameMove(gameId, nextMoveNumber, direction, snakePositions);

        boolean success = gameMoveDAO.createGameMove(gameMove);

        response.setContentType("application/json");
        JsonObject result = new JsonObject();
        result.addProperty("success", success);

        if (success) {
            result.addProperty("moveNumber", nextMoveNumber);
        } else {
            result.addProperty("error", "Failed to save move");
        }

        response.getWriter().write(result.toString());
    }
}