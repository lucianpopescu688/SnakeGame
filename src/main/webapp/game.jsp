<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.snakegame.model.Game" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.temporal.ChronoUnit" %>
<%@ page import="java.time.LocalDateTime" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Snake Game</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="game-container">
    <header class="game-header">
        <div class="header-left">
            <h1>Snake Game</h1>
            <p>Welcome, <%= session.getAttribute("username") %>!</p>
        </div>
        <div class="header-right">
            <a href="${pageContext.request.contextPath}/game?action=history" class="btn btn-secondary">Game History</a>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger" onclick="return confirm('Are you sure you want to logout?')">Logout</a>
        </div>
    </header>

    <div class="game-content">
        <div class="game-info">
            <div class="info-panel">
                <div class="info-item">
                    <label>Score:</label>
                    <span id="score">0</span>
                </div>
                <div class="info-item">
                    <label>Time:</label>
                    <span id="gameTime">00:00</span>
                </div>
                <div class="info-item">
                    <label>Status:</label>
                    <span id="gameStatus">
                            <%
                                Game currentGame = (Game) request.getAttribute("currentGame");
                                if (currentGame != null && currentGame.isActive()) {
                                    out.print("Playing");
                                } else {
                                    out.print("Not Playing");
                                }
                            %>
                        </span>
                </div>
            </div>

            <div class="game-controls">
                <% if (currentGame == null || !currentGame.isActive()) { %>
                <button id="newGameBtn" class="btn btn-success" onclick="startNewGame()">Start New Game</button>
                <% } else { %>
                <button id="endGameBtn" class="btn btn-warning" onclick="endGame()">End Game</button>
                <button id="pauseBtn" class="btn btn-secondary" onclick="togglePause()">Pause</button>
                <% } %>
            </div>
        </div>

        <div class="game-board-container">
            <canvas id="gameCanvas" width="600" height="600" style="border: 2px solid #333;"></canvas>
            <div id="gameOverlay" class="game-overlay" style="display: none;">
                <div class="overlay-content">
                    <h2 id="overlayTitle">Game Over</h2>
                    <p id="overlayMessage">Press Space to start a new game</p>
                    <button class="btn btn-primary" onclick="startNewGame()">New Game</button>
                </div>
            </div>
        </div>

        <div class="game-instructions">
            <h3>How to Play:</h3>
            <ul>
                <li>Use arrow keys or WASD to control the snake</li>
                <li>Eat the red food to grow and increase your score</li>
                <li>Avoid hitting the walls, obstacles, or yourself</li>
                <li>Gray squares are obstacles - avoid them!</li>
            </ul>

            <div class="controls-info">
                <h4>Controls:</h4>
                <p>↑ W - Up | ↓ S - Down | ← A - Left | → D - Right</p>
                <p>Space - Start New Game | P - Pause/Resume</p>
            </div>
        </div>
    </div>
</div>

<script>
    // Game state variables
    let gameActive = <%= currentGame != null && currentGame.isActive() ? "true" : "false" %>;
    let gameStartTime = <%= currentGame != null && currentGame.isActive() ? "new Date('" + currentGame.getStartTime().toString() + "')" : "null" %>;
    let currentScore = <%= currentGame != null ? currentGame.getScore() : 0 %>;
    let gameId = <%= currentGame != null ? currentGame.getId() : "null" %>;

    // Update initial display
    document.getElementById('score').textContent = currentScore;

    // Load and start the game
    window.onload = function() {
        if (gameActive && gameStartTime) {
            startGameTimer();
        }
    };
</script>
<script src="${pageContext.request.contextPath}/js/snake-game.js"></script>
</body>
</html>