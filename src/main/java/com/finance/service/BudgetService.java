package com.finance.service;

import com.finance.dao.BudgetDao;
import com.finance.dao.ExpenseDao;
import com.finance.model.Budget;
import com.finance.util.IdGenerator;
import com.finance.util.Money;
import com.finance.util.Validator;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

public class BudgetService {

    private static final List<String> PERIODS = List.of(Budget.PERIOD_MONTHLY, Budget.PERIOD_WEEKLY, Budget.PERIOD_ANNUAL);

    private final BudgetDao budgetDao = new BudgetDao();
    private final ExpenseDao expenseDao = new ExpenseDao();

    /**
     * The user's budgets, each with "spent" filled in from expenses of the same
     * category in the budget's current period (this week / month / year).
     */
    public List<Budget> listWithSpending(String userId) throws SQLException {
        List<Budget> budgets = budgetDao.findByUser(userId);
        LocalDate today = LocalDate.now();
        for (Budget b : budgets) {
            LocalDate[] range = currentPeriod(b.getPeriod(), today);
            BigDecimal spent = Money.of(expenseDao.sumForCategory(userId, b.getCategory(), range[0], range[1]));
            b.setSpent(spent);
            b.setPercentage(Money.percent(spent, b.getAmount()));
        }
        return budgets;
    }

    /** Creates a budget, or replaces the amount if the user already has one for this category and period. */
    public void save(String userId, String category, String amount, String period)
            throws ValidationException, SQLException {
        String cleanCategory = Validator.text(category, "Category", 20);
        BigDecimal cleanAmount = Validator.amount(amount);
        if (period == null || !PERIODS.contains(period)) {
            throw new ValidationException("Period must be Monthly, Weekly or Annual.");
        }

        Budget existing = budgetDao.findByCategoryAndPeriod(userId, cleanCategory, period);
        if (existing != null) {
            budgetDao.updateAmount(existing.getId(), userId, cleanAmount);
        } else {
            budgetDao.insert(new Budget(IdGenerator.next("BUD"), userId, cleanCategory, cleanAmount, period));
        }
    }

    public void delete(String id, String userId) throws ValidationException, SQLException {
        if (Validator.isBlank(id) || !budgetDao.delete(id, userId)) {
            throw new ValidationException("Budget not found.");
        }
    }

    /** Start and end date (inclusive) of the period that contains the given day. */
    static LocalDate[] currentPeriod(String period, LocalDate day) {
        return switch (period) {
            case Budget.PERIOD_WEEKLY -> new LocalDate[]{
                    day.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
                    day.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))};
            case Budget.PERIOD_ANNUAL -> new LocalDate[]{
                    day.with(TemporalAdjusters.firstDayOfYear()),
                    day.with(TemporalAdjusters.lastDayOfYear())};
            default -> new LocalDate[]{
                    day.with(TemporalAdjusters.firstDayOfMonth()),
                    day.with(TemporalAdjusters.lastDayOfMonth())};
        };
    }
}
