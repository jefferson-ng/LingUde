package com.sep.sep_backend.exercise.entity;

import com.sep.sep_backend.user.entity.Language;
import com.sep.sep_backend.user.entity.LanguageLevel;
import jakarta.persistence.*;
import jdk.jfr.ContentType;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import com.sep.sep_backend.exercise.entity.ExerciseContentType;


@Entity
@Table(name = "exercise_fill_blank")
public class ExerciseFillBlank {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_language", nullable = false, length = 10)
    private Language targetLanguage;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level", nullable = false, length = 10)
    private LanguageLevel difficultyLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type")
    private ExerciseContentType contentType;

    @Column(length = 50)
    private String topic;

    @Column(name = "xp_reward", nullable = false)
    private Integer xpReward = 10;

    @Column(name = "sentence_with_blank", nullable = false, length = 500)
    private String sentenceWithBlank;

    @Column(name = "correct_answer", nullable = false, length = 100)
    private String correctAnswer;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public ExerciseFillBlank() {
    }

    public ExerciseFillBlank(Language targetLanguage, LanguageLevel difficultyLevel,
                             String sentenceWithBlank, String correctAnswer) {
        this.targetLanguage = targetLanguage;
        this.difficultyLevel = difficultyLevel;
        this.sentenceWithBlank = sentenceWithBlank;
        this.correctAnswer = correctAnswer;
        this.xpReward = 10;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Language getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(Language targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    public LanguageLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(LanguageLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getXpReward() {
        return xpReward;
    }

    public void setXpReward(Integer xpReward) {
        this.xpReward = xpReward;
    }

    public String getSentenceWithBlank() {
        return sentenceWithBlank;
    }

    public void setSentenceWithBlank(String sentenceWithBlank) {
        this.sentenceWithBlank = sentenceWithBlank;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ExerciseContentType getContentType() {
        return contentType;
    }

    public void setContentType(ExerciseContentType contentType) {
        this.contentType = contentType;
    }
}