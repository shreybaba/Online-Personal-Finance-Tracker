package com.finance.filter;

import com.finance.model.User;
import com.finance.servlet.BaseServlet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Protects the role areas: not logged in -> redirect to /login, wrong role -> 403.
 * /user/* and /feedback/* are for USER, /advisor/* for ADVISOR, /admin/* for ADMIN.
 */
@WebFilter({"/user/*", "/feedback/*", "/advisor/*", "/admin/*"})
public class AuthFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute(BaseServlet.SESSION_USER);

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (!user.getRole().equals(requiredRole(request.getServletPath()))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Pages show private data: stop the browser from showing them from cache after logout
        response.setHeader("Cache-Control", "no-store");
        chain.doFilter(request, response);
    }

    private String requiredRole(String path) {
        if (path.startsWith("/admin/")) {
            return User.ROLE_ADMIN;
        }
        if (path.startsWith("/advisor/")) {
            return User.ROLE_ADVISOR;
        }
        return User.ROLE_USER;
    }
}
