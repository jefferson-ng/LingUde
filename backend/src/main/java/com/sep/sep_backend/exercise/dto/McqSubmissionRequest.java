package com.sep.sep_backend.exercise.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Represents a request for submitting an answer to a multiple-choice question (MCQ).
 *
 * This class is used to encapsulate the user's selected answer for an MCQ exercise.
 * The selected answer is stored in the `selectedAnswer` field, which is validated
 * to ensure it is not blank.
 *
 * Validation:
 * - The `selectedAnswer` field must not be blank.
 *
 * Primary usage is for processing MCQ submissions in systems that handle language
 * exercises or assessments.
 */
public class McqSubmissionRequest {
    @NotBlank
    private String selectedAnswer;

    public String getSelectedAnswer() { return selectedAnswer; }
    public void setSelectedAnswer(String selectedAnswer) { this.selectedAnswer = selectedAnswer; }
}
