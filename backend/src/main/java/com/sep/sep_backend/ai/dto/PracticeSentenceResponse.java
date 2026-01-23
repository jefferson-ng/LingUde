package com.sep.sep_backend.ai.dto;

import com.sep.sep_backend.user.entity.LanguageLevel;

import java.util.UUID;

/**
 * DTO for practice sentence suggestions from exercises.
 */
public class PracticeSentenceResponse {
    private UUID exerciseId;
    private String sentence;
    private LanguageLevel level;
    private String topic;
    private String contentType;

    public PracticeSentenceResponse() {
    }

    public PracticeSentenceResponse(UUID exerciseId, String sentence, LanguageLevel level, String topic, String contentType) {
        this.exerciseId = exerciseId;
        this.sentence = sentence;
        this.level = level;
        this.topic = topic;
        this.contentType = contentType;
    }

    public UUID getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(UUID exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public LanguageLevel getLevel() {
        return level;
    }

    public void setLevel(LanguageLevel level) {
        this.level = level;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
