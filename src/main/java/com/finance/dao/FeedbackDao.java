package com.finance.dao;

import com.finance.DBConnection;
import com.finance.model.Feedback;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDao {

    private static final String COLUMNS = "id, user_id, message, status, `date`";

    public void insert(Feedback f) throws SQLException {
        String sql = "INSERT INTO feedback (id, user_id, message, status, `date`) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, f.getId());
            ps.setString(2, f.getUserId());
            ps.setString(3, f.getMessage());
            ps.setString(4, f.getStatus());
            ps.setDate(5, Date.valueOf(f.getDate()));
            ps.executeUpdate();
        }
    }

    public boolean updateStatus(String id, String status) throws SQLException {
        String sql = "UPDATE feedback SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Feedback> findAll() throws SQLException {
        return query("SELECT " + COLUMNS + " FROM feedback ORDER BY `date` DESC, id DESC");
    }

    public List<Feedback> findRecent(int limit) throws SQLException {
        return query("SELECT " + COLUMNS + " FROM feedback ORDER BY `date` DESC, id DESC LIMIT " + limit);
    }

    public int countByStatus(String status) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM feedback WHERE status = ?")) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private List<Feedback> query(String sql) throws SQLException {
        List<Feedback> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Feedback(
                        rs.getString("id"),
                        rs.getString("user_id"),
                        rs.getString("message"),
                        rs.getString("status"),
                        rs.getDate("date").toLocalDate()));
            }
        }
        return list;
    }
}
