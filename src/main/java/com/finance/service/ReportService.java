package com.finance.service;

import com.finance.dao.BudgetDao;
import com.finance.dao.ExpenseDao;
import com.finance.model.Budget;
import com.finance.model.BudgetComparison;
import com.finance.model.CategoryTotal;
import com.finance.util.Money;
import com.finance.util.Validator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class ReportService {

    /** Everything the reports page shows. totalSpending is null when nothing matches the filter. */
    public record Report(BigDecimal totalSpending,
                         BigDecimal averageExpense,
                         List<CategoryTotal> categoryTotals,
                         List<BudgetComparison> budgetComparison) {
    }

    private final ExpenseDao expenseDao = new ExpenseDao();
    private final BudgetDao budgetDao = new BudgetDao();

    /** Dates are optional (empty = no limit); category is optional (empty = all). */
    public Report build(String userId, String startDate, String endDate, String category)
            throws ValidationException, SQLException {
        LocalDate from = Validator.optionalDate(startDate, "From date");
        LocalDate to = Validator.optionalDate(endDate, "To date");
        if (from != null && to != null && from.isAfter(to)) {
            throw new ValidationException("From date must be on or before To date.");
        }
        String cat = Validator.isBlank(category) ? null : category.trim();

        ExpenseDao.Summary summary = expenseDao.summarize(userId, from, to, cat);
        if (summary.count() == 0) {
            return new Report(null, null, List.of(), List.of());
        }

        BigDecimal total = Money.of(summary.total());
        BigDecimal average = total.divide(BigDecimal.valueOf(summary.count()), 2, RoundingMode.HALF_UP);

        List<CategoryTotal> categoryTotals = expenseDao.totalsByCategory(userId, from, to, cat);
        categoryTotals.forEach(r -> r.setPercentage(Money.percent(r.getAmount(), total)));

        // Compare each budget with what was spent in that category during the selected range
        List<BudgetComparison> comparison = new ArrayList<>();
        for (Budget b : budgetDao.findByUser(userId)) {
            if (cat != null && !cat.equalsIgnoreCase(b.getCategory())) {
                continue;
            }
            BigDecimal spent = Money.of(expenseDao.sumForCategory(userId, b.getCategory(), from, to));
            comparison.add(new BudgetComparison(b.getCategory() + " (" + b.getPeriod() + ")", b.getAmount(), spent));
        }

        return new Report(total, average, categoryTotals, comparison);
    }

    /** Categories used in the user's expenses and budgets, for the filter dropdown. */
    public List<String> categories(String userId) throws SQLException {
        TreeSet<String> names = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        names.addAll(expenseDao.distinctCategories(userId));
        budgetDao.findByUser(userId).forEach(b -> names.add(b.getCategory()));
        return new ArrayList<>(names);
    }
}
