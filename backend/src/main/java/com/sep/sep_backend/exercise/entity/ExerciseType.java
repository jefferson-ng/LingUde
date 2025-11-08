package com.sep.sep_backend.exercise.entity;

public enum ExerciseType {
    MCQ("Multiple Choice Question"),
    FILL_BLANK("Fill in the Blank");

    private final String displayName;

    ExerciseType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}