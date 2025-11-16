package com.sep.sep_backend.exercise.dto;

/**
 * Represents a response to a submission result for an exercise.
 *
 * This class encapsulates the result of an attempt by a user to complete an exercise.
 * It provides detailed feedback on whether the submission was correct,
 * the experience points (XP) earned, the correct answer, and additional feedback
 * to guide the user or offer further clarification.
 *
 * Attributes:
 * - `correct`: Indicates whether the user's submission was correct.
 * - `xpEarned`: Represents the number of experience points the user earned for the submission.
 * - `correctAnswer`: Specifies the correct answer to the exercise, if applicable.
 * - `feedback`: Provides additional messages or remarks about the submission, such as explanations or tips.
 *
 * This response is typically used in systems that assess exercises such as
 * multiple-choice questions (MCQs) or fill-in-the-blank exercises within a language learning context.
 */
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
