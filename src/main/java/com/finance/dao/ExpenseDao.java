package com.finance.dao;

import com.finance.DBConnection;
import com.finance.model.CategoryTotal;
import com.finance.model.Expense;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Every query that reads or changes a user's expenses is scoped by user_id,
 * so one user can never see or modify another user's rows.
 */
public class ExpenseDao {

    private static final String COLUMNS = "id, category, amount, `date`, user_id";

    public void insert(Expense e) throws SQLException {
        String sql = "INSERT INTO expenses (id, category, amount, `date`, user_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getId());
            ps.setString(2, e.getCategory());
            ps.setBigDecimal(3, e.getAmount());
            ps.setDate(4, Date.valueOf(e.getDate()));
            ps.setString(5, e.getUserId());
            ps.executeUpdate();
        }
    }

    /** Returns false if the expense does not exist or belongs to someone else. */
    public boolean update(Expense e) throws SQLException {
        String sql = "UPDATE expenses SET category = ?, amount = ?, `date` = ? WHERE id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getCategory());
            ps.setBigDecimal(2, e.getAmount());
            ps.setDate(3, Date.valueOf(e.getDate()));
            ps.setString(4, e.getId());
            ps.setString(5, e.getUserId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(String id, String userId) throws SQLException {
        String sql = "DELETE FROM expenses WHERE id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public Expense findById(String id, String userId) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM expenses WHERE id = ? AND user_id = ?";
        List<Expense> list = query(sql, id, userId);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<Expense> findByUser(String userId) throws SQLException {
        return query("SELECT " + COLUMNS + " FROM expenses WHERE user_id = ? ORDER BY `date` DESC, id DESC", userId);
    }

    public List<Expense> findRecent(String userId, int limit) throws SQLException {
        return query("SELECT " + COLUMNS + " FROM expenses WHERE user_id = ? ORDER BY `date` DESC, id DESC LIMIT " + limit,
                userId);
    }

    public BigDecimal sumByUser(String userId) throws SQLException {
        return sum("SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE user_id = ?", userId, null, null, null);
    }

    /** Sum for one category between two dates (inclusive). */
    public BigDecimal sumForCategory(String userId, String category, LocalDate from, LocalDate to) throws SQLException {
        return sum("SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE user_id = ?", userId, category, from, to);
    }

    /**
     * Totals per category, largest first. from, to and category are optional filters (null = no filter).
     */
    public List<CategoryTotal> totalsByCategory(String userId, LocalDate from, LocalDate to, String category)
            throws SQLException {
        Filter f = new Filter("SELECT category, SUM(amount) AS total FROM expenses WHERE user_id = ?", userId)
                .range(from, to)
                .category(category);
        String sql = f.sql + " GROUP BY category ORDER BY total DESC";
        List<CategoryTotal> rows = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            f.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new CategoryTotal(rs.getString("category"), rs.getBigDecimal("total")));
                }
            }
        }
        return rows;
    }

    /** Number of expenses and their total for a report. */
    public record Summary(int count, BigDecimal total) {
    }

    public Summary summarize(String userId, LocalDate from, LocalDate to, String category) throws SQLException {
        Filter f = new Filter("SELECT COUNT(*), COALESCE(SUM(amount), 0) FROM expenses WHERE user_id = ?", userId)
                .range(from, to)
                .category(category);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(f.sql)) {
            f.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return new Summary(rs.getInt(1), rs.getBigDecimal(2));
            }
        }
    }

    public List<String> distinctCategories(String userId) throws SQLException {
        String sql = "SELECT DISTINCT category FROM expenses WHERE user_id = ? ORDER BY category";
        List<String> categories = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    categories.add(rs.getString(1));
                }
            }
        }
        return categories;
    }

    public int countAll() throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM expenses");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private BigDecimal sum(String baseSql, String userId, String category, LocalDate from, LocalDate to)
            throws SQLException {
        Filter f = new Filter(baseSql, userId).range(from, to).category(category);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(f.sql)) {
            f.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getBigDecimal(1);
            }
        }
    }

    private List<Expense> query(String sql, String... params) throws SQLException {
        List<Expense> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setString(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Expense(
                            rs.getString("id"),
                            rs.getString("category"),
                            rs.getBigDecimal("amount"),
                            rs.getDate("date").toLocalDate(),
                            rs.getString("user_id")));
                }
            }
        }
        return list;
    }

    /** Builds an optional WHERE-clause extension while keeping every value as a bound parameter. */
    private static final class Filter {
        private String sql;
        private final List<Object> params = new ArrayList<>();

        Filter(String baseSql, String userId) {
            this.sql = baseSql;
            params.add(userId);
        }

        Filter range(LocalDate from, LocalDate to) {
            if (from != null) {
                sql += " AND `date` >= ?";
                params.add(Date.valueOf(from));
            }
            if (to != null) {
                sql += " AND `date` <= ?";
                params.add(Date.valueOf(to));
            }
            return this;
        }

        Filter category(String category) {
            if (category != null && !category.isBlank()) {
                sql += " AND category = ?";
                params.add(category);
            }
            return this;
        }

        void bind(PreparedStatement ps) throws SQLException {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
        }
    }
}
