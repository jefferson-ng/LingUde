package com.sep.sep_backend.user.dto;

import com.sep.sep_backend.user.entity.Language;
import com.sep.sep_backend.user.entity.LanguageLevel;

import java.time.LocalDate;

/**
 * Data Transfer Object for UserLearning information.
 * This DTO is used to transfer user learning data between the backend and frontend,
 * exposing user progress information such as XP, streak count, and learning preferences.
 */
public class UserLearningDTO {

    private String userId;
    private Language learningLanguage;
    private LanguageLevel currentLevel;
    private LanguageLevel targetLevel;
    private Integer xp;
    private Integer streakCount;
    private LocalDate lastActivityDate;

    // Constructors

    public UserLearningDTO() {
    }

    public UserLearningDTO(String userId, Language learningLanguage, LanguageLevel currentLevel,
                           LanguageLevel targetLevel, Integer xp, Integer streakCount, LocalDate lastActivityDate) {
        this.userId = userId;
        this.learningLanguage = learningLanguage;
        this.currentLevel = currentLevel;
        this.targetLevel = targetLevel;
        this.xp = xp;
        this.streakCount = streakCount;
        this.lastActivityDate = lastActivityDate;
    }



    // Getters and Setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Language getLearningLanguage() {
        return learningLanguage;
    }

    public void setLearningLanguage(Language learningLanguage) {
        this.learningLanguage = learningLanguage;
    }

    public LanguageLevel getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(LanguageLevel currentLevel) {
        this.currentLevel = currentLevel;
    }

    public LanguageLevel getTargetLevel() {
        return targetLevel;
    }

    public void setTargetLevel(LanguageLevel targetLevel) {
        this.targetLevel = targetLevel;
    }

    public Integer getXp() {
        return xp;
    }

    public void setXp(Integer xp) {
        this.xp = xp;
    }

    public Integer getStreakCount() {
        return streakCount;
    }

    public void setStreakCount(Integer streakCount) {
        this.streakCount = streakCount;
    }

    public LocalDate getLastActivityDate() {
        return lastActivityDate;
    }

    public void setLastActivityDate(LocalDate lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }
}
