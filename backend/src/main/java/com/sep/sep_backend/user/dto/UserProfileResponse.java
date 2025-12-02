package com.sep.sep_backend.user.dto;

import com.sep.sep_backend.user.entity.LanguageLevel;

import java.util.List;

/**
 * API response DTO for the learner's profile page.
 * <p>
 * This combines basic profile information (username, avatar)
 * with gamification data (XP, level, achievements).
 * </p>
 */
public class UserProfileResponse {

    /**
     * Username of the user (immutable login name).
     */
    private String username;

    /**
     * Display name shown on the profile page.
     * Can be null if the user never set it.
     */
    private String displayName;

    /**
     * URL to the user's avatar image.
     * Can be null if the user did not upload/choose an avatar.
     */
    private String avatarUrl;

    /**
     * Total XP points the user has earned.
     */
    private Integer xp;

    /**
     * Current language level of the user (A1, A2, B1, ...).
     */
    private LanguageLevel level;

    /**
     * List of achievements that the user has unlocked.
     */
    private List<AchievementSummaryDTO> achievements;

    // Constructors

    public UserProfileResponse() {
    }

    public UserProfileResponse(String username,
                               String displayName,
                               String avatarUrl,
                               Integer xp,
                               LanguageLevel level,
                               List<AchievementSummaryDTO> achievements) {
        this.username = username;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.xp = xp;
        this.level = level;
        this.achievements = achievements;
    }

    // Getters and setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Integer getXp() {
        return xp;
    }

    public void setXp(Integer xp) {
        this.xp = xp;
    }

    public LanguageLevel getLevel() {
        return level;
    }

    public void setLevel(LanguageLevel level) {
        this.level = level;
    }

    public List<AchievementSummaryDTO> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<AchievementSummaryDTO> achievements) {
        this.achievements = achievements;
    }
}

