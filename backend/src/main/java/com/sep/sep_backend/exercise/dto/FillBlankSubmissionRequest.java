package com.sep.sep_backend.exercise.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Represents a request for submitting an answer to a fill-in-the-blank exercise.
 *
 * This class is used to encapsulate the user's input in the form of an answer text
 * for fill-in-the-blank exercises. The `answerText` field contains the user's response
 * and is validated to ensure it is not blank.
 *
 * Validation:
 * - The `answerText` field must not be blank.
 *
 * Primary usage is for processing fill-in-the-blank submissions in systems handling
 * language exercises or assessments.
 */
public class FillBlankSubmissionRequest {
    @NotBlank
    private String answerText;

    public String getAnswerText() { return answerText; }
    public void setAnswerText(String answerText) { this.answerText = answerText; }
}
