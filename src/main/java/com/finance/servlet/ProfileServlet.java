package com.finance.servlet;

import com.finance.service.AuthService;
import com.finance.service.ValidationException;
import com.finance.util.Flash;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * GET  /user/profile          profile page (data comes from the session user)
 * POST /user/update-password  change password
 */
@WebServlet({"/user/profile", "/user/update-password"})
public class ProfileServlet extends BaseServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!"/user/profile".equals(request.getServletPath())) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        render(request, response, "user/profile");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!"/user/update-password".equals(request.getServletPath())) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        try {
            authService.changePassword(currentUser(request).getId(),
                    request.getParameter("oldPassword"), request.getParameter("newPassword"));
            Flash.success(request, "Password updated.");
        } catch (ValidationException e) {
            Flash.error(request, e.getMessage());
        } catch (SQLException e) {
            throw new ServletException("Could not update password", e);
        }
        redirect(request, response, "/user/profile");
    }
}
