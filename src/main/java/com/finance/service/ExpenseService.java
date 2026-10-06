package com.finance.service;

import com.finance.dao.ExpenseDao;
import com.finance.model.CategoryTotal;
import com.finance.model.Expense;
import com.finance.util.IdGenerator;
import com.finance.util.Money;
import com.finance.util.Validator;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ExpenseService {

    private final ExpenseDao expenseDao = new ExpenseDao();

    public List<Expense> listForUser(String userId) throws SQLException {
        return expenseDao.findByUser(userId);
    }

    public List<Expense> recentForUser(String userId, int limit) throws SQLException {
        return expenseDao.findRecent(userId, limit);
    }

    public Expense find(String id, String userId) throws SQLException {
        return Validator.isBlank(id) ? null : expenseDao.findById(id, userId);
    }

    public BigDecimal totalForUser(String userId) throws SQLException {
        return Money.of(expenseDao.sumByUser(userId));
    }

    /** All-time spending per category with each category's share of the total. */
    public List<CategoryTotal> categoryBreakdown(String userId) throws SQLException {
        List<CategoryTotal> rows = expenseDao.totalsByCategory(userId, null, null, null);
        BigDecimal total = rows.stream().map(CategoryTotal::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        rows.forEach(r -> r.setPercentage(Money.percent(r.getAmount(), total)));
        return rows;
    }

    /** Creates a new expense, or updates the existing one when id is present. */
    public void save(String userId, String id, String category, String amount, String date)
            throws ValidationException, SQLException {
        Expense expense = new Expense(
                null,
                Validator.text(category, "Category", 15),
                Validator.amount(amount),
                Validator.date(date, "Date"),
                userId);

        if (Validator.isBlank(id)) {
            expense.setId(IdGenerator.next("EXP"));
            expenseDao.insert(expense);
        } else {
            expense.setId(id);
            if (!expenseDao.update(expense)) {
                throw new ValidationException("Expense not found.");
            }
        }
    }

    public void delete(String id, String userId) throws ValidationException, SQLException {
        if (Validator.isBlank(id) || !expenseDao.delete(id, userId)) {
            throw new ValidationException("Expense not found.");
        }
    }
}
