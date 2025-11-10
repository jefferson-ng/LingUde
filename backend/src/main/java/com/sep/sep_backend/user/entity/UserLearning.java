package com.sep.sep_backend.user.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

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

    // Constructors
    public UserLearning() {
    }

    public UserLearning(User user) {
        this.user = user;
        this.xp = 0;
        this.streakCount = 0;
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
}