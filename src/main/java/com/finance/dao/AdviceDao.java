package com.finance.dao;

import com.finance.DBConnection;
import com.finance.model.Advice;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdviceDao {

    private static final String COLUMNS = "id, advisor_id, message, `date`, user_id";

    public void insert(Advice a) throws SQLException {
        String sql = "INSERT INTO advice (id, advisor_id, message, `date`, user_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getId());
            ps.setString(2, a.getAdvisorId());
            ps.setString(3, a.getMessage());
            ps.setDate(4, Date.valueOf(a.getDate()));
            ps.setString(5, a.getUserId());
            ps.executeUpdate();
        }
    }

    /** Advisors can only delete advice they wrote. */
    public boolean delete(String id, String advisorId) throws SQLException {
        String sql = "DELETE FROM advice WHERE id = ? AND advisor_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, advisorId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Advice> findByUser(String userId) throws SQLException {
        return query("SELECT " + COLUMNS + " FROM advice WHERE user_id = ? ORDER BY `date` DESC, id DESC", userId);
    }

    public List<Advice> findByAdvisor(String advisorId) throws SQLException {
        return query("SELECT " + COLUMNS + " FROM advice WHERE advisor_id = ? ORDER BY `date` DESC, id DESC", advisorId);
    }

    private List<Advice> query(String sql, String param) throws SQLException {
        List<Advice> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Advice(
                            rs.getString("id"),
                            rs.getString("advisor_id"),
                            rs.getString("message"),
                            rs.getDate("date").toLocalDate(),
                            rs.getString("user_id")));
                }
            }
        }
        return list;
    }
}
