package org.example.model;

import java.util.List;

public class Exam {
    private final int id;
    private final String title;
    private final java.util.List<Question> questions;

    public Exam(int id, String title, java.util.List<Question> questions) {
        this.id = id; this.title = title; this.questions = List.copyOf(questions);
    }

    public int getId(){ return id; }
    public String getTitle(){ return title; }
    public java.util.List<Question> getQuestions(){ return questions; }
}
