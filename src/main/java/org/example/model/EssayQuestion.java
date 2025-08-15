package org.example.model;

public class EssayQuestion extends Question {
    private final String expectedKeywords;

    public EssayQuestion(int id, int examId, String text, String expectedKeywords) {
        super(id, examId, text, QuestionType.ESSAY);
        this.expectedKeywords = expectedKeywords;
    }

    public String getExpectedKeywords(){ return expectedKeywords; }
}
