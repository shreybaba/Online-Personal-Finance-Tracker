package com.finance.dao;

import com.finance.DBConnection;
import com.finance.model.Budget;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BudgetDao {

    private static final String COLUMNS = "id, user_id, category, amount, period";

    public void insert(Budget b) throws SQLException {
        String sql = "INSERT INTO budgets (id, user_id, category, amount, period) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.getId());
            ps.setString(2, b.getUserId());
            ps.setString(3, b.getCategory());
            ps.setBigDecimal(4, b.getAmount());
            ps.setString(5, b.getPeriod());
            ps.executeUpdate();
        }
    }

    public boolean updateAmount(String id, String userId, BigDecimal amount) throws SQLException {
        String sql = "UPDATE budgets SET amount = ? WHERE id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, amount);
            ps.setString(2, id);
            ps.setString(3, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(String id, String userId) throws SQLException {
        String sql = "DELETE FROM budgets WHERE id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public Budget findByCategoryAndPeriod(String userId, String category, String period) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM budgets WHERE user_id = ? AND category = ? AND period = ?";
        List<Budget> list = query(sql, userId, category, period);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<Budget> findByUser(String userId) throws SQLException {
        return query("SELECT " + COLUMNS + " FROM budgets WHERE user_id = ? ORDER BY category, period", userId);
    }

    private List<Budget> query(String sql, String... params) throws SQLException {
        List<Budget> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setString(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Budget(
                            rs.getString("id"),
                            rs.getString("user_id"),
                            rs.getString("category"),
                            rs.getBigDecimal("amount"),
                            rs.getString("period")));
                }
            }
        }
        return list;
    }
}
