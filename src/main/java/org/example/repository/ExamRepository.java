package org.example.repository;

import org.example.model.Exam;

public interface ExamRepository {
    Exam findExam(int examId);
}
