package org.example.dto;

public record SubmitResponse(int examId, int userId, int score, int correctCount, int totalQuestions) {}
