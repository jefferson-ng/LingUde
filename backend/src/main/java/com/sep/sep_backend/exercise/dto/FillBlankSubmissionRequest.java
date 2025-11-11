package com.sep.sep_backend.exercise.dto;

import jakarta.validation.constraints.NotBlank;

public class FillBlankSubmissionRequest {
    @NotBlank
    private String answerText;

    public String getAnswerText() { return answerText; }
    public void setAnswerText(String answerText) { this.answerText = answerText; }
}
