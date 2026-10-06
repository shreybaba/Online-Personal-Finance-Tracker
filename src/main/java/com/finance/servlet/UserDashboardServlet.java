package com.finance.servlet;

import com.finance.model.Budget;
import com.finance.service.AdviceService;
import com.finance.service.BudgetService;
import com.finance.service.ExpenseService;
import com.finance.util.Money;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/user/dashboard")
public class UserDashboardServlet extends BaseServlet {

    private static final int RECENT_EXPENSES = 5;

    private final ExpenseService expenseService = new ExpenseService();
    private final BudgetService budgetService = new BudgetService();
    private final AdviceService adviceService = new AdviceService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userId = currentUser(request).getId();
        try {
            request.setAttribute("totalExpenses", expenseService.totalForUser(userId));
            request.setAttribute("recentExpenses", expenseService.recentForUser(userId, RECENT_EXPENSES));
            request.setAttribute("categoryBreakdown", expenseService.categoryBreakdown(userId));
            request.setAttribute("adviceList", adviceService.forUser(userId));

            // Budget cards: totals across all budgets, "spent" counted within each budget's current period
            List<Budget> budgets = budgetService.listWithSpending(userId);
            if (!budgets.isEmpty()) {
                BigDecimal totalBudget = budgets.stream().map(Budget::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal spent = budgets.stream().map(Budget::getSpent).reduce(BigDecimal.ZERO, BigDecimal::add);

                Budget overall = new Budget(null, userId, "All budgets", Money.of(totalBudget), null);
                overall.setSpent(Money.of(spent));
                overall.setPercentage(Money.percent(spent, totalBudget));

                request.setAttribute("totalBudget", Money.of(totalBudget));
                request.setAttribute("remainingBudget", Money.of(totalBudget.subtract(spent)));
                request.setAttribute("activeBudget", overall);
            }
        } catch (SQLException e) {
            throw new ServletException("Could not load dashboard", e);
        }
        render(request, response, "user/dashboard");
    }
}
