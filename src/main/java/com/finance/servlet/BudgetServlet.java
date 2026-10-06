package com.finance.servlet;

import com.finance.service.BudgetService;
import com.finance.service.ValidationException;
import com.finance.util.Flash;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * GET  /user/budgets        list with spending
 * POST /user/add-budget     create, or replace the amount for an existing category + period
 * POST /user/delete-budget  delete
 */
@WebServlet({"/user/budgets", "/user/add-budget", "/user/delete-budget"})
public class BudgetServlet extends BaseServlet {

    private final BudgetService budgetService = new BudgetService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!"/user/budgets".equals(request.getServletPath())) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        try {
            request.setAttribute("budgetsList", budgetService.listWithSpending(currentUser(request).getId()));
        } catch (SQLException e) {
            throw new ServletException("Could not load budgets", e);
        }
        render(request, response, "user/budgets");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userId = currentUser(request).getId();
        try {
            switch (request.getServletPath()) {
                case "/user/add-budget" -> {
                    budgetService.save(userId, request.getParameter("category"),
                            request.getParameter("amount"), request.getParameter("period"));
                    Flash.success(request, "Budget saved.");
                }
                case "/user/delete-budget" -> {
                    budgetService.delete(request.getParameter("id"), userId);
                    Flash.success(request, "Budget deleted.");
                }
                default -> {
                    response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                    return;
                }
            }
        } catch (ValidationException e) {
            Flash.error(request, e.getMessage());
        } catch (SQLException e) {
            throw new ServletException("Could not update budget", e);
        }
        redirect(request, response, "/user/budgets");
    }
}
