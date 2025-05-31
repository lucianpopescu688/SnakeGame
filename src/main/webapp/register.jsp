<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Snake Game - Register</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">
    <div class="form-container">
        <h1>Snake Game</h1>
        <h2>Register</h2>

        <% if (request.getAttribute("error") != null) { %>
        <div class="error-message">
            <%= request.getAttribute("error") %>
        </div>
        <% } %>

        <form action="${pageContext.request.contextPath}/register" method="post" class="form" onsubmit="return validateForm()">
            <div class="form-group">
                <label for="username">Username:</label>
                <input type="text"
                       id="username"
                       name="username"
                       value="<%= request.getAttribute("username") != null ? request.getAttribute("username") : "" %>"
                       required
                       minlength="3"
                       maxlength="50">
                <small>Username must be at least 3 characters long</small>
            </div>

            <div class="form-group">
                <label for="password">Password:</label>
                <input type="password"
                       id="password"
                       name="password"
                       required
                       minlength="4"
                       maxlength="100">
                <small>Password must be at least 4 characters long</small>
            </div>

            <div class="form-group">
                <label for="confirmPassword">Confirm Password:</label>
                <input type="password"
                       id="confirmPassword"
                       name="confirmPassword"
                       required
                       minlength="4"
                       maxlength="100">
            </div>

            <div class="form-group">
                <button type="submit" class="btn btn-primary">Register</button>
            </div>
        </form>

        <div class="form-footer">
            <p>Already have an account? <a href="${pageContext.request.contextPath}/login">Login here</a></p>
        </div>
    </div>
</div>

<script>
    function validateForm() {
        var password = document.getElementById('password').value;
        var confirmPassword = document.getElementById('confirmPassword').value;

        if (password !== confirmPassword) {
            alert('Passwords do not match!');
            return false;
        }

        return true;
    }
</script>
</body>
</html>