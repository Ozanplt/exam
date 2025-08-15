package org.example.repository;

import org.example.model.Exam;
import org.example.model.Question;

import org.example.util.Log;

import java.sql.*;
import java.util.List;

public class JdbcExamRepository implements ExamRepository {
    private final String url, user, pass;
    private final QuestionRepository questionRepository;

    public JdbcExamRepository(String url, String user, String pass) {
        this.url = url; this.user = user; this.pass = pass;
        this.questionRepository = new JdbcQuestionRepository(url, user, pass);
    }

    @Override
    public Exam findExam(int examId) {
        String sql = "SELECT id, title FROM Exams WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                String title = rs.getString("title");
                List<Question> questions = questionRepository.findByExamId(examId);
                return new Exam(examId, title, questions);
            }
        } catch (SQLException e) {
            Log.ERROR.log("DB error in findExam", e);
            throw new RuntimeException("DB error");
        }
    }
}
