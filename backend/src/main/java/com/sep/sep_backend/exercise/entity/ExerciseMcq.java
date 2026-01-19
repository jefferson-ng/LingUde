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
@Table(name = "exercise_mcq")
public class ExerciseMcq {

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

    @Column(name = "question_text", nullable = false, length = 500)
    private String questionText;

    @Column(name = "correct_answer", nullable = false, length = 100)
    private String correctAnswer;

    @Column(name = "wrong_option_1", nullable = false, length = 100)
    private String wrongOption1;

    @Column(name = "wrong_option_2", nullable = false, length = 100)
    private String wrongOption2;

    @Column(name = "wrong_option_3", nullable = false, length = 100)
    private String wrongOption3;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public ExerciseMcq() {
    }

    public ExerciseMcq(Language targetLanguage, LanguageLevel difficultyLevel, String questionText,
                       String correctAnswer, String wrongOption1, String wrongOption2, String wrongOption3) {
        this.targetLanguage = targetLanguage;
        this.difficultyLevel = difficultyLevel;
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.wrongOption1 = wrongOption1;
        this.wrongOption2 = wrongOption2;
        this.wrongOption3 = wrongOption3;
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

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getWrongOption1() {
        return wrongOption1;
    }

    public void setWrongOption1(String wrongOption1) {
        this.wrongOption1 = wrongOption1;
    }

    public String getWrongOption2() {
        return wrongOption2;
    }

    public void setWrongOption2(String wrongOption2) {
        this.wrongOption2 = wrongOption2;
    }

    public String getWrongOption3() {
        return wrongOption3;
    }

    public void setWrongOption3(String wrongOption3) {
        this.wrongOption3 = wrongOption3;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ExerciseContentType getContentType() { return contentType; }

    public void setContentType(ExerciseContentType contentType) { this.contentType = contentType; }
}