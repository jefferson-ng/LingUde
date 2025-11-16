package com.sep.sep_backend.exercise.dto;

import com.sep.sep_backend.exercise.entity.ExerciseType;

import java.util.UUID;

/**
 * Represents a summarized response for an exercise, providing key details such as type,
 * language, difficulty level, and other metadata relevant for listing exercises.
 *
 * This class is designed to encapsulate brief information about an exercise, primarily
 * for use in endpoints returning a collection of exercises, such as lists or previews.
 *
 * Attributes:
 * - `id`: Unique identifier of the exercise.
 * - `type`: Type of the exercise (e.g., MCQ, Fill in the Blank).
 * - `targetLanguage`: The target language associated with the exercise.
 * - `difficultyLevel`: Skill level or difficulty of the exercise (e.g., A1, B2).
 * - `topic`: Subject or context of the exercise (e.g., Vocabulary, Opposites).
 * - `xpReward`: XP points rewarded for completing the exercise.
 * - `previewText`: A short description or preview to show as a hint or teaser for the exercise.
 */
public class ExerciseSummaryResponse {
    private UUID id;
    private ExerciseType type;
    private String targetLanguage;
    private String difficultyLevel;
    private String topic;
    private Integer xpReward;
    private String previewText;

    public ExerciseSummaryResponse() {}

    public ExerciseSummaryResponse(UUID id, ExerciseType type, String targetLanguage, String difficultyLevel,
                                   String topic, Integer xpReward, String previewText) {
        this.id = id;
        this.type = type;
        this.targetLanguage = targetLanguage;
        this.difficultyLevel = difficultyLevel;
        this.topic = topic;
        this.xpReward = xpReward;
        this.previewText = previewText;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public ExerciseType getType() { return type; }
    public void setType(ExerciseType type) { this.type = type; }

    public String getTargetLanguage() { return targetLanguage; }
    public void setTargetLanguage(String targetLanguage) { this.targetLanguage = targetLanguage; }

    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public Integer getXpReward() { return xpReward; }
    public void setXpReward(Integer xpReward) { this.xpReward = xpReward; }

    public String getPreviewText() { return previewText; }
    public void setPreviewText(String previewText) { this.previewText = previewText; }
}
