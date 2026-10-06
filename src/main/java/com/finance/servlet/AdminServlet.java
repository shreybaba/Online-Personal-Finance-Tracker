package com.finance.servlet;

import com.finance.service.FeedbackService;
import com.finance.service.UserService;
import com.finance.service.ValidationException;
import com.finance.util.Flash;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * GET  /admin/dashboard               system counts, newest users, latest feedback
 * GET  /admin/users                   all accounts
 * POST /admin/delete-user             delete an account and its data
 * GET  /admin/feedback                all feedback tickets
 * POST /admin/update-feedback-status  change a ticket's status
 */
@WebServlet({"/admin/dashboard", "/admin/users", "/admin/delete-user",
        "/admin/feedback", "/admin/update-feedback-status"})
public class AdminServlet extends BaseServlet {

    private static final int DASHBOARD_ROWS = 5;

    private final UserService userService = new UserService();
    private final FeedbackService feedbackService = new FeedbackService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            switch (request.getServletPath()) {
                case "/admin/dashboard" -> {
                    request.setAttribute("totalUserCount", userService.count());
                    request.setAttribute("totalExpensesCount", userService.expenseCount());
                    request.setAttribute("pendingFeedbackCount", feedbackService.pendingCount());
                    request.setAttribute("recentUserList", userService.recent(DASHBOARD_ROWS));
                    request.setAttribute("feedbackList", feedbackService.recent(DASHBOARD_ROWS));
                    render(request, response, "admin/dashboard");
                }
                case "/admin/users" -> {
                    request.setAttribute("userList", userService.all());
                    render(request, response, "admin/users");
                }
                case "/admin/feedback" -> {
                    request.setAttribute("feedbackList", feedbackService.all());
                    render(request, response, "admin/feedback");
                }
                default -> response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            }
        } catch (SQLException e) {
            throw new ServletException("Could not load admin page", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        try {
            switch (request.getServletPath()) {
                case "/admin/delete-user" -> {
                    try {
                        userService.delete(id, currentUser(request).getId());
                        Flash.success(request, "User deleted.");
                    } catch (ValidationException e) {
                        Flash.error(request, e.getMessage());
                    }
                    redirect(request, response, "/admin/users");
                }
                case "/admin/update-feedback-status" -> {
                    try {
                        feedbackService.updateStatus(id, request.getParameter("status"));
                        Flash.success(request, "Feedback status updated.");
                    } catch (ValidationException e) {
                        Flash.error(request, e.getMessage());
                    }
                    redirect(request, response, "/admin/feedback");
                }
                default -> response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            }
        } catch (SQLException e) {
            throw new ServletException("Could not update", e);
        }
    }
}
