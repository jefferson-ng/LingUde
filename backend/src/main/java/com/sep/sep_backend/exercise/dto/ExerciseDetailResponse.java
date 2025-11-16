package com.sep.sep_backend.exercise.dto;

import com.sep.sep_backend.exercise.entity.ExerciseType;

import java.util.List;
import java.util.UUID;

/**
 * Represents the detailed response for an exercise, including its core properties and
 * specific fields based on the exercise type.
 *
 * This class provides information about an exercise such as its type, target language,
 * difficulty level, topic, XP reward, and details specific to the exercise type.
 *
 * An exercise can have variations, such as multiple-choice questions (MCQs) or fill-in-the-blank.
 * Depending on the exercise type, relevant fields like `questionText` and `options` (for MCQs)
 * or `sentenceWithBlank` (for fill-in-the-blank) are included.
 */
public class ExerciseDetailResponse {
    private UUID id;
    private ExerciseType type;
    private String targetLanguage;
    private String difficultyLevel;
    private String topic;
    private Integer xpReward;

    // MCQ fields
    private String questionText;
    private List<String> options;

    // Fill-Blank fields
    private String sentenceWithBlank;

    public ExerciseDetailResponse() {}

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

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public String getSentenceWithBlank() { return sentenceWithBlank; }
    public void setSentenceWithBlank(String sentenceWithBlank) { this.sentenceWithBlank = sentenceWithBlank; }
}
