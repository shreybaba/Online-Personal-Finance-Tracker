package com.finance.servlet;

import com.finance.model.User;
import com.finance.service.AuthService;
import com.finance.service.ValidationException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends BaseServlet {

    private static final int REMEMBER_ME_SECONDS = 7 * 24 * 60 * 60;

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = currentUser(request);
        if (user != null) {
            redirect(request, response, homePathFor(user));
            return;
        }
        render(request, response, "auth/login");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            User user = authService.login(request.getParameter("email"), request.getParameter("password"));
            startSession(request, user, "true".equals(request.getParameter("rememberMe")));
            redirect(request, response, homePathFor(user));
        } catch (ValidationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            render(request, response, "auth/login");
        } catch (SQLException e) {
            throw new ServletException("Login failed", e);
        }
    }

    /** Replaces any existing session so a pre-login session id cannot be reused (session fixation). */
    static void startSession(HttpServletRequest request, User user, boolean rememberMe) {
        HttpSession old = request.getSession(false);
        if (old != null) {
            old.invalidate();
        }
        HttpSession session = request.getSession(true);
        session.setAttribute(SESSION_USER, user);
        if (rememberMe) {
            session.setMaxInactiveInterval(REMEMBER_ME_SECONDS);
        }
    }
}
