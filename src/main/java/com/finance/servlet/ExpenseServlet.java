package com.finance.servlet;

import com.finance.model.Expense;
import com.finance.service.ExpenseService;
import com.finance.service.ValidationException;
import com.finance.util.Flash;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * GET  /user/expenses        list
 * GET  /user/add-expense     empty form, or edit form with ?id=
 * POST /user/add-expense     create (no id) or update (with id)
 * POST /user/delete-expense  delete
 */
@WebServlet({"/user/expenses", "/user/add-expense", "/user/delete-expense"})
public class ExpenseServlet extends BaseServlet {

    private final ExpenseService expenseService = new ExpenseService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userId = currentUser(request).getId();
        try {
            switch (request.getServletPath()) {
                case "/user/expenses" -> {
                    request.setAttribute("expensesList", expenseService.listForUser(userId));
                    render(request, response, "user/expenses");
                }
                case "/user/add-expense" -> showForm(request, response, userId);
                default -> response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            }
        } catch (SQLException e) {
            throw new ServletException("Could not load expenses", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userId = currentUser(request).getId();
        String id = request.getParameter("id");
        try {
            switch (request.getServletPath()) {
                case "/user/add-expense" -> {
                    try {
                        expenseService.save(userId, id, request.getParameter("category"),
                                request.getParameter("amount"), request.getParameter("date"));
                        Flash.success(request, id == null || id.isBlank() ? "Expense added." : "Expense updated.");
                        redirect(request, response, "/user/expenses");
                    } catch (ValidationException e) {
                        request.setAttribute("errorMessage", e.getMessage());
                        showForm(request, response, userId);
                    }
                }
                case "/user/delete-expense" -> {
                    try {
                        expenseService.delete(id, userId);
                        Flash.success(request, "Expense deleted.");
                    } catch (ValidationException e) {
                        Flash.error(request, e.getMessage());
                    }
                    redirect(request, response, "/user/expenses");
                }
                default -> response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            }
        } catch (SQLException e) {
            throw new ServletException("Could not save expense", e);
        }
    }

    private void showForm(HttpServletRequest request, HttpServletResponse response, String userId)
            throws ServletException, IOException, SQLException {
        String id = request.getParameter("id");
        if (id != null && !id.isBlank()) {
            Expense expense = expenseService.find(id, userId);
            if (expense == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            request.setAttribute("expense", expense);
        }
        render(request, response, "user/add-expense");
    }
}
