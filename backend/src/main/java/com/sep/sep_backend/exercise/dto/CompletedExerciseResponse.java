package com.sep.sep_backend.exercise.dto;

import com.sep.sep_backend.exercise.entity.ExerciseType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO representing a single completed exercise for a user.
 *
 * This is used in the response of:
 *   GET /api/exercises/completed
 *
 * It deliberately contains only the data that the frontend needs
 * to display completion state and basic progress, and not the full
 * UserProgress entity or internal database fields.
 */
public class CompletedExerciseResponse {

    // ---------------------------
    // Fields exposed in the API
    // ---------------------------

    /**
     * ID of the completed exercise.
     */
    private UUID exerciseId;

    /**
     * Type of the exercise (MCQ, FILL_BLANK, ...).
     */
    private ExerciseType type;

    /**
     * Amount of XP the user earned for this exercise.
     */
    private int xpEarned;

    /**
     * Timestamp when the exercise was completed.
     * Can be null if, for some reason, completion was not recorded.
     */
    private LocalDateTime completedAt;


    // ---------------------------
    // Constructors
    // ---------------------------

    /**
     * No-args constructor needed for JSON serialization/deserialization.
     */
    public CompletedExerciseResponse() {
    }

    /**
     * Convenience constructor for quickly mapping from a UserProgress entity.
     *
     * @param exerciseId ID of the exercise
     * @param type       type of the exercise
     * @param xpEarned   XP earned for this exercise
     * @param completedAt timestamp of completion
     */
    public CompletedExerciseResponse(UUID exerciseId,
                                     ExerciseType type,
                                     int xpEarned,
                                     LocalDateTime completedAt) {
        this.exerciseId = exerciseId;
        this.type = type;
        this.xpEarned = xpEarned;
        this.completedAt = completedAt;
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

    public int getXpEarned() {
        return xpEarned;
    }

    public void setXpEarned(int xpEarned) {
        this.xpEarned = xpEarned;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
