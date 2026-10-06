package com.finance.dao;

import com.finance.DBConnection;
import com.finance.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    private static final String COLUMNS = "id, name, role, password, email";

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM `user` WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public User findById(String id) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM `user` WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public boolean emailExists(String email) throws SQLException {
        return findByEmail(email) != null;
    }

    public void insert(User user) throws SQLException {
        String sql = "INSERT INTO `user` (id, name, role, password, email) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getId());
            ps.setString(2, user.getName());
            ps.setString(3, user.getRole());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getEmail());
            ps.executeUpdate();
        }
    }

    public void updatePassword(String userId, String newPassword) throws SQLException {
        String sql = "UPDATE `user` SET password = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, userId);
            ps.executeUpdate();
        }
    }

    public List<User> findAll() throws SQLException {
        return query("SELECT " + COLUMNS + " FROM `user` ORDER BY id DESC", null);
    }

    public List<User> findByRole(String role) throws SQLException {
        return query("SELECT " + COLUMNS + " FROM `user` WHERE role = ? ORDER BY name", role);
    }

    /** IDs embed the creation time, so ordering by id gives the newest accounts first. */
    public List<User> findRecent(int limit) throws SQLException {
        return query("SELECT " + COLUMNS + " FROM `user` ORDER BY id DESC LIMIT " + limit, null);
    }

    public int countAll() throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM `user`");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    /** Deletes a user and everything that belongs to them, in one transaction. */
    public boolean deleteWithRelatedData(String userId) throws SQLException {
        String[] related = {
                "DELETE FROM expenses WHERE user_id = ?",
                "DELETE FROM budgets WHERE user_id = ?",
                "DELETE FROM feedback WHERE user_id = ?",
                "DELETE FROM advice WHERE user_id = ? OR advisor_id = ?",
        };
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                for (String sql : related) {
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        int placeholders = (int) sql.chars().filter(ch -> ch == '?').count();
                        for (int i = 1; i <= placeholders; i++) {
                            ps.setString(i, userId);
                        }
                        ps.executeUpdate();
                    }
                }
                int deleted;
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM `user` WHERE id = ?")) {
                    ps.setString(1, userId);
                    deleted = ps.executeUpdate();
                }
                conn.commit();
                return deleted > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    private List<User> query(String sql, String param) throws SQLException {
        List<User> users = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (param != null) {
                ps.setString(1, param);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(map(rs));
                }
            }
        }
        return users;
    }

    private User map(ResultSet rs) throws SQLException {
        return new User(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("role"),
                rs.getString("password"),
                rs.getString("email"));
    }
}
