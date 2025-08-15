package org.example.dto;

import java.util.List;

public record SubmitRequest(int examId, java.util.List<AnswerDTO> answers) {}
