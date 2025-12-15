package com.sep.sep_backend.admin.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Full detailed view of a user for the admin user-details page.
 *
 * This DTO aggregates data from multiple user-related aggregates:
 *  - User (basic identity + role)
 *  - UserProfile (profile information)
 *  - UserLearning (language learning state)
 *  - UserAchievement (unlocked achievements)
 *
 * It is returned by GET /api/admin/users/{userId}.
 */
public class AdminUserDetailResponse {

    // Basic user info
    private UUID userId;
    private String username;
    private String email;
    private String role;

    // Profile info
    private String displayName;
    private String avatarUrl;
    private String bio;
    private String city;
    private String country;

    // Learning info
    private String learningLanguage;
    private String currentLevel;
    private String targetLevel;
    private Integer xp;
    private Integer streakCount;
    private LocalDate lastActivityDate;

    // Achievements
    private List<AdminUserAchievementDTO> achievements;

    public AdminUserDetailResponse() {
        // Default constructor for JSON frameworks
    }

    public AdminUserDetailResponse(
            UUID userId,
            String username,
            String email,
            String role,
            String displayName,
            String avatarUrl,
            String bio,
            String city,
            String country,
            String learningLanguage,
            String currentLevel,
            String targetLevel,
            Integer xp,
            Integer streakCount,
            LocalDate lastActivityDate,
            List<AdminUserAchievementDTO> achievements
    ) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.bio = bio;
        this.city = city;
        this.country = country;
        this.learningLanguage = learningLanguage;
        this.currentLevel = currentLevel;
        this.targetLevel = targetLevel;
        this.xp = xp;
        this.streakCount = streakCount;
        this.lastActivityDate = lastActivityDate;
        this.achievements = achievements;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLearningLanguage() {
        return learningLanguage;
    }

    public void setLearningLanguage(String learningLanguage) {
        this.learningLanguage = learningLanguage;
    }

    public String getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(String currentLevel) {
        this.currentLevel = currentLevel;
    }

    public String getTargetLevel() {
        return targetLevel;
    }

    public void setTargetLevel(String targetLevel) {
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

    public List<AdminUserAchievementDTO> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<AdminUserAchievementDTO> achievements) {
        this.achievements = achievements;
    }
}
