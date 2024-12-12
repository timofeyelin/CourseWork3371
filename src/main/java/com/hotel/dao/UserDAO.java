package main.java.com.hotel.dao;

import main.java.com.hotel.model.User;
import main.java.com.hotel.util.DatabaseConnection;

import java.sql.*;

public class UserDAO {
    private final String url = "jdbc:postgresql://localhost:5432/hotel_database";
    private final String user = "postgres";
    private final String password = "123";

    public void addUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getRole());
            pstmt.executeUpdate();
        }
    }

    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(url, this.user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPasswordHash(rs.getString("password"));
                user.setRole(rs.getString("role"));
                return user;
            }
        }
        return null;
    }

    // Дополнительные методы для обновления, удаления пользователей и т.д.
}