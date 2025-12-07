package com.sep.sep_backend.exercise.entity;

import com.sep.sep_backend.user.entity.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents the progress of ONE user on ONE exercise.
 * Maps the USER_PROGRESS table from the database.
 *
 * One row per (user, exerciseId, exerciseType).
 * Tracks:
 *  - completed or not
 *  - when it was completed
 *  - XP earned
 *  - created timestamp
 */

@Entity
@Table(name = "user_progress",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "exercise_id", "exercise_type"}))
public class UserProgress {

    // Primary key for this progress entry (UUID)
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // The user associated with this progress entry (FK → USER table)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ID of the exercise (UUID) — can belong to either MCQ or FillBlank
    @Column(name = "exercise_id", nullable = false)
    private UUID exerciseId;

    // Type of the exercise: MCQ or FILL_BLANK
    @Enumerated(EnumType.STRING)
    @Column(name = "exercise_type", nullable = false, length = 20)
    private ExerciseType exerciseType;

    // Timestamp of first completion (null if not completed yet)
    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted = false;

    // XP earned from completing this exercise (only on first completion)
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // When this progress row was created (first attempt)
    @Column(name = "xp_earned", nullable = false)
    private Integer xpEarned = 0;

    // When this progress row was created (first attempt)
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Default constructor for JPA
    public UserProgress() {
    }

    /**
     * Constructor used when a user first starts an exercise.
     * At this moment:
     *  - isCompleted = false
     *  - xpEarned = 0
     */
    public UserProgress(User user, UUID exerciseId, ExerciseType exerciseType) {
        this.user = user;
        this.exerciseId = exerciseId;
        this.exerciseType = exerciseType;
        this.isCompleted = false;
        this.xpEarned = 0;
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

    public UUID getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(UUID exerciseId) {
        this.exerciseId = exerciseId;
    }

    public ExerciseType getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(ExerciseType exerciseType) {
        this.exerciseType = exerciseType;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public Integer getXpEarned() {
        return xpEarned;
    }

    public void setXpEarned(Integer xpEarned) {
        this.xpEarned = xpEarned;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // =========================
    //   BUSINESS HELPER METHOD
    // =========================

    /**
     * Mark this exercise as completed for the user.
     * This will:
     *  - set isCompleted = true
     *  - set completedAt = now (only first time)
     *  - set xpEarned (only first time)
     *
     * We follow the design decision:
     *  "XP is only awarded on first successful completion."
     */
    public void markCompleted(Integer xpToAward) {
        if (Boolean.FALSE.equals(this.isCompleted)) {
            this.isCompleted = true;
            this.completedAt = LocalDateTime.now();
            this.xpEarned = (xpToAward != null ? xpToAward : 0);
        }
        // If already completed, do nothing → no XP farming
    }

}