package org.example.model;

public abstract class Question {
    protected int id;
    protected int examId;
    protected String text;
    protected QuestionType type;

    public Question(int id, int examId, String text, QuestionType type) {
        this.id = id; this.examId = examId; this.text = text; this.type = type;
    }
    public int getId(){ return id; }
    public int getExamId(){ return examId; }
    public String getText(){ return text; }
    public QuestionType getType(){ return type; }
}
