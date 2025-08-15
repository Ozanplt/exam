package org.example.dto;

import java.util.List;

public record ExamDeliveryDTO(int examId, String title, List<QuestionDTO> questions) {}
