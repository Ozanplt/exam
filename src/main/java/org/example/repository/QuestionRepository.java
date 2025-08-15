package org.example.repository;

import org.example.model.Question;

import java.util.List;

public interface QuestionRepository {
    java.util.List<Question> findByExamId(int examId);
}
