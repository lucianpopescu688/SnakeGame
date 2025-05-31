<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.snakegame.model.Game" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Game History - Snake Game</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">
    <header class="page-header">
        <div class="header-left">
            <h1>Game History</h1>
            <p>Welcome, <%= session.getAttribute("username") %>!</p>
        </div>
        <div class="header-right">
            <a href="${pageContext.request.contextPath}/game" class="btn btn-primary">Back to Game</a>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger" onclick="return confirm('Are you sure you want to logout?')">Logout</a>
        </div>
    </header>

    <div class="history-content">
        <%
            @SuppressWarnings("unchecked")
            List<Game> games = (List<Game>) request.getAttribute("games");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

            if (games == null || games.isEmpty()) {
        %>
        <div class="no-games">
            <h3>No games played yet!</h3>
            <p>Start playing to see your game history here.</p>
            <a href="${pageContext.request.contextPath}/game" class="btn btn-success">Start Playing</a>
        </div>
        <%
        } else {
        %>
        <div class="games-summary">
            <h3>Your Statistics</h3>
            <div class="stats-grid">
                <div class="stat-item">
                    <label>Total Games:</label>
                    <span><%= games.size() %></span>
                </div>
                <div class="stat-item">
                    <label>Best Score:</label>
                    <span><%= games.stream().mapToInt(Game::getScore).max().orElse(0) %></span>
                </div>
                <div class="stat-item">
                    <label>Average Score:</label>
                    <span><%= String.format("%.1f", games.stream().mapToInt(Game::getScore).average().orElse(0.0)) %></span>
                </div>
                <div class="stat-item">
                    <label>Completed Games:</label>
                    <span><%= games.stream().mapToInt(g -> g.isActive() ? 0 : 1).sum() %></span>
                </div>
            </div>
        </div>

        <div class="games-table-container">
            <table class="games-table">
                <thead>
                <tr>
                    <th>Game #</th>
                    <th>Start Time</th>
                    <th>End Time</th>
                    <th>Duration</th>
                    <th>Score</th>
                    <th>Status</th>
                </tr>
                </thead>
                <tbody>
                <%
                    for (int i = 0; i < games.size(); i++) {
                        Game game = games.get(i);
                        String duration = "N/A";

                        if (!game.isActive() && game.getEndTime() != null) {
                            long seconds = game.getTotalTimeSeconds();
                            duration = String.format("%02d:%02d", seconds / 60, seconds % 60);
                        } else if (game.isActive()) {
                            duration = "Playing...";
                        }
                %>
                <tr class="<%= game.isActive() ? "active-game" : "" %>">
                    <td><%= games.size() - i %></td>
                    <td><%= game.getStartTime().format(formatter) %></td>
                    <td>
                        <%= game.getEndTime() != null ? game.getEndTime().format(formatter) :
                                (game.isActive() ? "In Progress" : "N/A") %>
                    </td>
                    <td><%= duration %></td>
                    <td class="score-cell"><%= game.getScore() %></td>
                    <td class="status-cell">
                                        <span class="status-badge <%= game.isActive() ? "status-active" : "status-completed" %>">
                                            <%= game.isActive() ? "Active" : "Completed" %>
                                        </span>
                    </td>
                </tr>
                <%
                    }
                %>
                </tbody>
            </table>
        </div>
        <%
            }
        %>
    </div>
</div>
</body>
</html>