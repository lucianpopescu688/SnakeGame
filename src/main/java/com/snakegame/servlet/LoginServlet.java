package com.snakegame.servlet;

import com.snakegame.dao.UserDAO;
import com.snakegame.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Forward to login page
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String action = request.getParameter("action");

        // Input validation
        if (username == null || username.trim().isEmpty()) {
            request.setAttribute("error", "Username is required");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Password is required");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        username = username.trim();

        if ("register".equals(action)) {
            // Registration logic
            if (username.length() < 3) {
                request.setAttribute("error", "Username must be at least 3 characters long");
                request.getRequestDispatcher("login.jsp").forward(request, response);
                return;
            }

            if (password.length() < 4) {
                request.setAttribute("error", "Password must be at least 4 characters long");
                request.getRequestDispatcher("login.jsp").forward(request, response);
                return;
            }

            // Check if user already exists
            User existingUser = userDAO.getUserByUsername(username);
            if (existingUser != null) {
                request.setAttribute("error", "Username already exists");
                request.getRequestDispatcher("login.jsp").forward(request, response);
                return;
            }

            // Create new user
            User newUser = new User(username, password);
            boolean created = userDAO.createUser(newUser);

            if (created) {
                request.setAttribute("success", "Registration successful! Please login.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Registration failed. Please try again.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }

        } else {
            // Login logic
            User user = userDAO.authenticate(username, password);

            if (user != null) {
                // Authentication successful
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                session.setAttribute("userId", user.getId());
                session.setAttribute("username", user.getUsername());

                // Set session timeout to 30 minutes
                session.setMaxInactiveInterval(30 * 60);

                response.sendRedirect("game.jsp");
            } else {
                // Authentication failed
                request.setAttribute("error", "Invalid username or password");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        }
    }
}