package org.example.repository;

import org.example.model.User;
import org.example.util.Log;

import java.sql.*;

public class JdbcUserRepository implements UserRepository {
    private final String url, user, pass;

    public JdbcUserRepository(String url, String user, String pass) {
        this.url = url; this.user = user; this.pass = pass;
    }

    @Override
    public User findByUsername(String username) {
        String sql = "SELECT id, username, password_hash FROM Users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(url, this.user, this.pass);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password_hash"));
            }
        } catch (SQLException e) {
            Log.ERROR.log("DB error in findByUsername", e);
            throw new RuntimeException("DB error");
        }
    }
}
