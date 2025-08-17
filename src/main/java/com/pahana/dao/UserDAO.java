package com.pahana.dao;

import com.pahana.model.User;
import java.sql.*;

public class UserDAO {
    private static UserDAO instance;
    private Connection conn;
    private volatile Boolean hasRoleColumn;

    private UserDAO() {
        conn = DBConnectionFactory.getConnection();
    }

    private boolean hasRoleColumn() {
        if (hasRoleColumn != null)
            return hasRoleColumn.booleanValue();
        synchronized (this) {
            if (hasRoleColumn != null)
                return hasRoleColumn.booleanValue();
            boolean exists = false;
            try {
                DatabaseMetaData meta = conn.getMetaData();
                try (ResultSet cols = meta.getColumns(null, null, "users", "role")) {
                    exists = cols.next();
                }
            } catch (SQLException e) {
            }
            hasRoleColumn = exists;
            return exists;
        }
    }

    public static synchronized UserDAO getInstance() {
        if (instance == null) {
            instance = new UserDAO();
        }
        return instance;
    }

    public User getUserByUsername(String username) {
        try {
            PreparedStatement ps;
            if (hasRoleColumn()) {
                ps = conn.prepareStatement("SELECT id, username, password, role FROM users WHERE username = ?");
            } else {
                ps = conn.prepareStatement("SELECT id, username, password FROM users WHERE username = ?");
            }
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (hasRoleColumn()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role"));
                } else {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUserById(int id) {
        try {
            PreparedStatement ps;
            if (hasRoleColumn()) {
                ps = conn.prepareStatement("SELECT id, username, password, role FROM users WHERE id = ?");
            } else {
                ps = conn.prepareStatement("SELECT id, username, password FROM users WHERE id = ?");
            }
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (hasRoleColumn()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role"));
                } else {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean usernameExists(String username) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM users WHERE username = ?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createUser(String username, String password, String role) {
        try {
            if (hasRoleColumn()) {
                String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, username);
                    ps.setString(2, password);
                    ps.setString(3, role);
                    return ps.executeUpdate() > 0;
                }
            } else {
                String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, username);
                    ps.setString(2, password);
                    return ps.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUsername(int id, String newUsername) {
        String sql = "UPDATE users SET username = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newUsername);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePassword(int id, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateRole(int id, String role) {
        if (!hasRoleColumn())
            return false;
        String sql = "UPDATE users SET role = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public java.util.List<User> getAllUsers() {
        java.util.List<User> list = new java.util.ArrayList<>();
        String sql = hasRoleColumn() ? "SELECT id, username, password, role FROM users ORDER BY username"
                : "SELECT id, username, password FROM users ORDER BY username";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                if (hasRoleColumn()) {
                    list.add(new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"),
                            rs.getString("role")));
                } else {
                    list.add(new User(rs.getInt("id"), rs.getString("username"), rs.getString("password")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
