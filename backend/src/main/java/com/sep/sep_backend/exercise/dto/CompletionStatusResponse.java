package com.sep.sep_backend.exercise.dto;

import com.sep.sep_backend.exercise.entity.ExerciseType;

import java.util.UUID;

/**
 * DTO representing the completion status of a single exercise
 * for the authenticated user.
 *
 * This is used in the response of:
 *   GET /api/exercises/{exerciseId}/completed
 *
 * It wraps the boolean "completed" flag together with the exercise
 * metadata, so the frontend has everything it needs in one object.
 */
public class CompletionStatusResponse {

    /**
     * ID of the exercise that was checked.
     */
    private UUID exerciseId;

    /**
     * Type of the exercise (MCQ, FILL_BLANK, ...).
     */
    private ExerciseType type;

    /**
     * Flag indicating whether the user has completed this exercise.
     */
    private boolean completed;


    // ---------------------------
    // Constructors
    // ---------------------------

    /**
     * No-args constructor for JSON (de)serialization.
     */
    public CompletionStatusResponse() {
    }

    /**
     * Convenience constructor used when building the response in the controller.
     *
     * @param exerciseId ID of the exercise
     * @param type       type of the exercise
     * @param completed  whether the user has completed it
     */
    public CompletionStatusResponse(UUID exerciseId,
                                    ExerciseType type,
                                    boolean completed) {
        this.exerciseId = exerciseId;
        this.type = type;
        this.completed = completed;
    }


    // ---------------------------
    // Getters and setters
    // ---------------------------

    public UUID getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(UUID exerciseId) {
        this.exerciseId = exerciseId;
    }

    public ExerciseType getType() {
        return type;
    }

    public void setType(ExerciseType type) {
        this.type = type;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
