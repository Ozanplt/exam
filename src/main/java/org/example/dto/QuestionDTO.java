package org.example.dto;

import org.example.model.QuestionType;
import java.util.List;

public record QuestionDTO(int id, QuestionType type, String text, List<String> options) {}
