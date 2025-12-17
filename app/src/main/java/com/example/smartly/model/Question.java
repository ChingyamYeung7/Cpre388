package com.example.smartly.model;

import java.util.Collections;
import java.util.List;

public class Question {

    public enum Type {
        MULTIPLE_CHOICE,
        DRAG_DROP_ORDER
    }

    private final Type type;
    private final String courseId;

    private final String lesson;
    private final String prompt;

    private final String correctFeedback;
    private final String wrongFeedback;

    // MCQ
    private final String[] options;
    private final int correctIndex;

    // Drag & drop
    private final List<String> dragItems;
    private final List<String> correctOrder;

    // Backward-compatible MCQ constructor (your old code)
    public Question(String lesson, String[] options, int correctIndex, String courseId) {
        this(
                Type.MULTIPLE_CHOICE,
                courseId,
                lesson,
                "Choose the best answer:",
                options,
                correctIndex,
                null,
                null,
                "Correct!",
                "Incorrect."
        );
    }

    public static Question mcq(
            String courseId,
            String lesson,
            String prompt,
            String[] options,
            int correctIndex,
            String correctFeedback,
            String wrongFeedback
    ) {
        return new Question(
                Type.MULTIPLE_CHOICE,
                courseId,
                lesson,
                prompt,
                options,
                correctIndex,
                null,
                null,
                correctFeedback,
                wrongFeedback
        );
    }

    public static Question dragDropOrder(
            String courseId,
            String lesson,
            String prompt,
            List<String> dragItems,
            List<String> correctOrder,
            String correctFeedback,
            String wrongFeedback
    ) {
        return new Question(
                Type.DRAG_DROP_ORDER,
                courseId,
                lesson,
                prompt,
                null,
                -1,
                dragItems,
                correctOrder,
                correctFeedback,
                wrongFeedback
        );
    }

    private Question(
            Type type,
            String courseId,
            String lesson,
            String prompt,
            String[] options,
            int correctIndex,
            List<String> dragItems,
            List<String> correctOrder,
            String correctFeedback,
            String wrongFeedback
    ) {
        this.type = type;
        this.courseId = courseId;
        this.lesson = lesson;
        this.prompt = prompt;
        this.options = options;
        this.correctIndex = correctIndex;
        this.dragItems = dragItems == null ? null : Collections.unmodifiableList(dragItems);
        this.correctOrder = correctOrder == null ? null : Collections.unmodifiableList(correctOrder);
        this.correctFeedback = correctFeedback;
        this.wrongFeedback = wrongFeedback;
    }

    // ✅ THESE METHODS WERE MISSING
    public Type getType() { return type; }

    public String getCourseId() { return courseId; }
    public String getLesson() { return lesson; }
    public String getPrompt() { return prompt; }

    public String[] getOptions() { return options; }
    public int getCorrectIndex() { return correctIndex; }

    public List<String> getDragItems() { return dragItems; }
    public List<String> getCorrectOrder() { return correctOrder; }

    public String getCorrectFeedback() { return correctFeedback; }
    public String getWrongFeedback() { return wrongFeedback; }
}
