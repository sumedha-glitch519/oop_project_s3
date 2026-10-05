package dao;

import database.DatabaseConnection;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Handles all database access for the "users" table.
 */
public class UserDAO {

    /**
     * Validates username/password against the users table.
     * Returns the matching User object, or null if invalid.
     * Uses PreparedStatement to prevent SQL injection.
     */
    public User validateLogin(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role")
                    );
                }
            }
        }
        return null;
    }

    /** Looks up a username by user_id (used to show "Pharmacist: ..." on a bill). */
    public String getUsernameById(int userId) throws SQLException {
        String sql = "SELECT username FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }
        }
        return "Unknown";
    }

    // ==================================================================
    // MERGE: added from login_module (Admin module)
    // ==================================================================
    // Returns all users (for the Manage Users table)
    public ArrayList<User> getAllUsers() throws SQLException {
        ArrayList<User> list = new ArrayList<>();
        String sql = "SELECT user_id, username, role FROM users ORDER BY user_id";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new User(rs.getInt("user_id"), rs.getString("username"), rs.getString("role")));
            }
        }
        return list;
    }

    // Checks whether a username is already taken
    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Adds a new user (ADMIN or PHARMACIST)
    public void addUser(String username, String password, String role) throws SQLException {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            ps.executeUpdate();
        }
    }

    // Deletes a user by id
    public void deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
