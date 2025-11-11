package com.sep.sep_backend.exercise.dto;

import com.sep.sep_backend.exercise.entity.ExerciseType;

import java.util.UUID;

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
