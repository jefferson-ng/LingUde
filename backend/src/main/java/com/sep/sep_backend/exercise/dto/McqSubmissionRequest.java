package com.sep.sep_backend.exercise.dto;

import jakarta.validation.constraints.NotBlank;

public class McqSubmissionRequest {
    @NotBlank
    private String selectedAnswer;

    public String getSelectedAnswer() { return selectedAnswer; }
    public void setSelectedAnswer(String selectedAnswer) { this.selectedAnswer = selectedAnswer; }
}
