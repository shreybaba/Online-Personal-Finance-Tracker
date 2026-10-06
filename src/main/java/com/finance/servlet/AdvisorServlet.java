package com.finance.servlet;

import com.finance.model.Advice;
import com.finance.model.User;
import com.finance.service.AdviceService;
import com.finance.service.ExpenseService;
import com.finance.service.UserService;
import com.finance.service.ValidationException;
import com.finance.util.Flash;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

/**
 * GET  /advisor/dashboard      advice issued by this advisor + stats
 * GET  /advisor/advice         client list, optional ?selectedUserId= to inspect expenses
 * POST /advisor/send-advice    create advice (advisor id comes from the session)
 * POST /advisor/delete-advice  delete own advice
 */
@WebServlet({"/advisor/dashboard", "/advisor/advice", "/advisor/send-advice", "/advisor/delete-advice"})
public class AdvisorServlet extends BaseServlet {

    private final AdviceService adviceService = new AdviceService();
    private final ExpenseService expenseService = new ExpenseService();
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String advisorId = currentUser(request).getId();
        try {
            switch (request.getServletPath()) {
                case "/advisor/dashboard" -> {
                    List<Advice> advice = adviceService.byAdvisor(advisorId);
                    request.setAttribute("adviceList", advice);
                    request.setAttribute("adviceCount", advice.size());
                    request.setAttribute("uniqueUsersAdvised", advice.stream().map(Advice::getUserId).distinct().count());
                    render(request, response, "advisor/dashboard");
                }
                case "/advisor/advice" -> showAdvicePage(request, response, advisorId);
                default -> response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            }
        } catch (SQLException e) {
            throw new ServletException("Could not load advisor page", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String advisorId = currentUser(request).getId();
        String targetUserId = request.getParameter("user_id");
        try {
            switch (request.getServletPath()) {
                case "/advisor/send-advice" -> {
                    try {
                        adviceService.send(advisorId, targetUserId,
                                request.getParameter("date"), request.getParameter("message"));
                        Flash.success(request, "Advice sent.");
                    } catch (ValidationException e) {
                        Flash.error(request, e.getMessage());
                    }
                    String query = targetUserId == null || targetUserId.isBlank() ? ""
                            : "?selectedUserId=" + URLEncoder.encode(targetUserId.trim(), StandardCharsets.UTF_8);
                    redirect(request, response, "/advisor/advice" + query);
                }
                case "/advisor/delete-advice" -> {
                    try {
                        adviceService.delete(request.getParameter("id"), advisorId);
                        Flash.success(request, "Advice deleted.");
                    } catch (ValidationException e) {
                        Flash.error(request, e.getMessage());
                    }
                    redirect(request, response, "/advisor/advice");
                }
                default -> response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            }
        } catch (SQLException e) {
            throw new ServletException("Could not update advice", e);
        }
    }

    private void showAdvicePage(HttpServletRequest request, HttpServletResponse response, String advisorId)
            throws ServletException, IOException, SQLException {
        List<User> clients = userService.clients();
        request.setAttribute("userList", clients);
        request.setAttribute("adviceList", adviceService.byAdvisor(advisorId));

        String selectedUserId = request.getParameter("selectedUserId");
        if (selectedUserId != null && !selectedUserId.isBlank()) {
            // Only let advisors inspect accounts with the USER role
            boolean isClient = clients.stream().anyMatch(u -> u.getId().equals(selectedUserId));
            if (isClient) {
                request.setAttribute("selectedUserId", selectedUserId);
                request.setAttribute("selectedUserExpenses", expenseService.listForUser(selectedUserId));
            } else {
                request.setAttribute("errorMessage", "No user account found with ID " + selectedUserId + ".");
            }
        }
        render(request, response, "advisor/advice");
    }
}
