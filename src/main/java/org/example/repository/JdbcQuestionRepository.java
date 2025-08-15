package org.example.repository;

import org.example.model.*;
import org.example.util.Log;

import java.sql.*;
import java.util.*;

public class JdbcQuestionRepository implements QuestionRepository {
    private final String url, user, pass;

    public JdbcQuestionRepository(String url, String user, String pass) {
        this.url = url; this.user = user; this.pass = pass;
    }

    @Override
    public java.util.List<Question> findByExamId(int examId) {
        String sql = "SELECT id, exam_id, type, text, options_json, correct_answer, expected_keywords FROM Questions WHERE exam_id = ?";
        java.util.List<Question> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String type = rs.getString("type");
                    if ("MC".equalsIgnoreCase(type)) {
                        java.util.List<String> options = parseOptions(rs.getString("options_json"));
                        list.add(new MultipleChoiceQuestion(
                                rs.getInt("id"),
                                rs.getInt("exam_id"),
                                rs.getString("text"),
                                options,
                                rs.getString("correct_answer")
                        ));
                    } else if ("ESSAY".equalsIgnoreCase(type)) {
                        list.add(new EssayQuestion(
                                rs.getInt("id"),
                                rs.getInt("exam_id"),
                                rs.getString("text"),
                                rs.getString("expected_keywords")
                        ));
                    }
                }
            }
            return list;
        } catch (SQLException e) {
            Log.ERROR.log("DB error in findByExamId", e);
            throw new RuntimeException("DB error");
        }
    }

    private java.util.List<String> parseOptions(String json) {
        if (json == null || json.isBlank()) return java.util.List.of();
        try {
            return org.example.util.JsonUtil.mapper().readValue(json, java.util.List.class);
        } catch (Exception e) {
            return java.util.List.of();
        }
    }
}
