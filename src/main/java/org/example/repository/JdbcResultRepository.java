package org.example.repository;

import org.example.model.Result;
import org.example.util.Log;

import java.sql.*;

public class JdbcResultRepository implements ResultRepository {
    private final String url, user, pass;

    public JdbcResultRepository(String url, String user, String pass) {
        this.url = url; this.user = user; this.pass = pass;
    }

    @Override
    public void save(Result r) {
        String sql = "INSERT INTO Results(exam_id, user_id, score, correct_count, total_questions, created_at) VALUES(?,?,?,?,?, NOW())";
        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.examId());
            ps.setInt(2, r.userId());
            ps.setInt(3, r.score());
            ps.setInt(4, r.correctCount());
            ps.setInt(5, r.totalQuestions());
            ps.executeUpdate();
        } catch (SQLException e) {
            Log.ERROR.log("DB error in save result", e);
            throw new RuntimeException("DB error");
        }
    }
}
