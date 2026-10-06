package com.finance.servlet;

import com.finance.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/** Shared helpers for the application's servlets. */
public abstract class BaseServlet extends HttpServlet {

    public static final String SESSION_USER = "user";

    /** The logged-in user; AuthFilter guarantees this is set for protected URLs. */
    protected User currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (User) session.getAttribute(SESSION_USER);
    }

    /** Forwards to /WEB-INF/views/{view}.jsp, e.g. render(req, resp, "user/dashboard"). */
    protected void render(HttpServletRequest request, HttpServletResponse response, String view)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/" + view + ".jsp").forward(request, response);
    }

    /** Redirects to a path inside the application, e.g. redirect(req, resp, "/user/expenses"). */
    protected void redirect(HttpServletRequest request, HttpServletResponse response, String path)
            throws IOException {
        response.sendRedirect(request.getContextPath() + path);
    }

    protected static String homePathFor(User user) {
        return switch (user.getRole()) {
            case User.ROLE_ADMIN -> "/admin/dashboard";
            case User.ROLE_ADVISOR -> "/advisor/dashboard";
            default -> "/user/dashboard";
        };
    }
}
