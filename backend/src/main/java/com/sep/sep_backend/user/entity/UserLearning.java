package com.sep.sep_backend.user.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;


/**
 * Tracks a user's language learning configuration and progress:
 * - selected learning language
 * - current and target CEFR level
 * - XP and streak for gamification
 */
@Entity
@Table(name = "user_learning")
public class UserLearning {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "learning_language",  length = 10)
    private Language learningLanguage;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_level", length = 10)
    private LanguageLevel currentLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_level", length = 10)
    private LanguageLevel targetLevel;


    @Column(nullable = false)
    private Integer xp = 0;

    @Column(name = "streak_count", nullable = false)
    private Integer streakCount = 0;

    @Column(name = "last_activity_date")
    private LocalDate lastActivityDate;

    @Column(name = "completed_levels", length = 500)
    private String completedLevels;

    // Constructors
    public UserLearning() {
    }

    /**
     * That already satisfies:
     *- “User has a language_level field”
     *- “Default is A1 if nothing selected”
     *- “Stored persistently in DB”
     */
    public UserLearning(User user) {
        this.user = user;
        this.xp = 0;
        this.streakCount = 0;
        this.currentLevel = LanguageLevel.A1;   // ensure default
        this.targetLevel = LanguageLevel.A1;    // ensure default
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public String getCompletedLevels() {
        return completedLevels;
    }

    public void setCompletedLevels(String completedLevels) {
        this.completedLevels = completedLevels;
    }
}