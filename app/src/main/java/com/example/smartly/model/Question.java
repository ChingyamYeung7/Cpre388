package com.example.smartly.model;

public class Question {

    private final String explanation;   // teaching text BEFORE the options
    private final String[] options;     // A, B, C...
    private final int correctIndex;     // which option is correct
    private final String courseId;      // which course this belongs to

    public Question(String explanation, String[] options, int correctIndex, String courseId) {
        this.explanation = explanation;
        this.options = options;
        this.correctIndex = correctIndex;
        this.courseId = courseId;
    }

    public String getExplanation() {
        return explanation;
    }

    public String[] getOptions() {
        return options;
    }

    public int getCorrectIndex() {
        return correctIndex;
    }

    public String getCourseId() {
        return courseId;
    }
}
