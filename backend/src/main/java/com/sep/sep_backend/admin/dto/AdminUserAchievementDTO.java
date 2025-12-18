package com.sep.sep_backend.admin.dto;

import java.time.LocalDateTime;

/**
 * Small DTO used inside AdminUserDetailResponse to expose
 * which achievements a user has unlocked and when.
 *
 * This mirrors the important fields from Achievement + UserAchievement
 * that are relevant for the admin view.
 */
public class AdminUserAchievementDTO {

    private String code;
    private String title;
    private String description;
    private LocalDateTime earnedAt;

    public AdminUserAchievementDTO() {
        // Default constructor for JSON frameworks
    }

    public AdminUserAchievementDTO(
            String code,
            String title,
            String description,
            LocalDateTime earnedAt
    ) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.earnedAt = earnedAt;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEarnedAt() {
        return earnedAt;
    }

    public void setEarnedAt(LocalDateTime earnedAt) {
        this.earnedAt = earnedAt;
    }
}
