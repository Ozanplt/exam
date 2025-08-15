package org.example.dto;

import java.util.List;

public record ExamResponse(int examId, String title, List<QuestionDTO> questions) {}
