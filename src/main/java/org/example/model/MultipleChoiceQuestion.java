package org.example.model;

import java.util.List;

public class MultipleChoiceQuestion extends Question {
    private final java.util.List<String> options;
    private final String correctOption;

    public MultipleChoiceQuestion(int id, int examId, String text, java.util.List<String> options, String correctOption) {
        super(id, examId, text, QuestionType.MULTIPLE_CHOICE);
        this.options = List.copyOf(options);
        this.correctOption = correctOption;
    }

    public java.util.List<String> getOptions(){ return options; }
    public String getCorrectOption(){ return correctOption; }
}
