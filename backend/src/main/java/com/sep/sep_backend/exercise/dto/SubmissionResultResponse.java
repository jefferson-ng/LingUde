package com.sep.sep_backend.exercise.dto;

public class SubmissionResultResponse {
    private boolean correct;
    private int xpEarned;
    private String correctAnswer;
    private String feedback;

    public SubmissionResultResponse() {}

    public SubmissionResultResponse(boolean correct, int xpEarned, String correctAnswer, String feedback) {
        this.correct = correct;
        this.xpEarned = xpEarned;
        this.correctAnswer = correctAnswer;
        this.feedback = feedback;
    }

    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }

    public int getXpEarned() { return xpEarned; }
    public void setXpEarned(int xpEarned) { this.xpEarned = xpEarned; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
}
