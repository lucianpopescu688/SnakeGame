<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Snake Game - Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">
    <div class="form-container">
        <h1>Snake Game</h1>
        <h2>Login</h2>

        <% if (request.getAttribute("error") != null) { %>
        <div class="error-message">
            <%= request.getAttribute("error") %>
        </div>
        <% } %>

        <% if (request.getAttribute("success") != null) { %>
        <div class="success-message">
            <%= request.getAttribute("success") %>
        </div>
        <% } %>

        <form action="${pageContext.request.contextPath}/login" method="post" class="form">
            <div class="form-group">
                <label for="username">Username:</label>
                <input type="text"
                       id="username"
                       name="username"
                       value="<%= request.getAttribute("username") != null ? request.getAttribute("username") : "" %>"
                       required
                       minlength="3"
                       maxlength="50">
            </div>

            <div class="form-group">
                <label for="password">Password:</label>
                <input type="password"
                       id="password"
                       name="password"
                       required
                       minlength="4"
                       maxlength="100">
            </div>

            <div class="form-group">
                <button type="submit" class="btn btn-primary">Login</button>
            </div>
        </form>

        <div class="form-footer">
            <p>Don't have an account? <a href="${pageContext.request.contextPath}/register">Register here</a></p>
        </div>
    </div>
</div>
</body>
</html>