package com.example.project_f1;

public class QuizQuestion {
    public final String question;
    public final String[] options;
    public final int correctIndex;
    public final String explanation;

    public QuizQuestion(String question, String[] options, int correctIndex, String explanation) {
        this.question = question;
        this.options = options;
        this.correctIndex = correctIndex;
        this.explanation = explanation;
    }
}
