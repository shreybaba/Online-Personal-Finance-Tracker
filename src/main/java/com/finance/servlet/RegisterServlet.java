package com.finance.servlet;

import com.finance.model.User;
import com.finance.service.AuthService;
import com.finance.service.ValidationException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends BaseServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = currentUser(request);
        if (user != null) {
            redirect(request, response, homePathFor(user));
            return;
        }
        render(request, response, "auth/register");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            User user = authService.register(
                    request.getParameter("name"),
                    request.getParameter("email"),
                    request.getParameter("password"),
                    request.getParameter("role"));
            LoginServlet.startSession(request, user, false);
            redirect(request, response, homePathFor(user));
        } catch (ValidationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            render(request, response, "auth/register");
        } catch (SQLException e) {
            throw new ServletException("Registration failed", e);
        }
    }
}
