package com.sep.sep_backend.user.dto;

import com.sep.sep_backend.user.entity.AchievementType;

import java.time.LocalDateTime;

/**
 * Data transfer object that represents an achievement with its unlock status.
 * <p>
 * Used for the profile page to display ALL achievements (both locked and unlocked).
 * Locked achievements appear grayed out in the UI.
 * </p>
 */
public class AchievementWithStatusDTO {

    /**
     * Technical identifier for the achievement, e.g. "FIRST_LESSON".
     */
    private String code;

    /**
     * Human-readable title of the achievement.
     */
    private String title;

    /**
     * Short description explaining the achievement.
     */
    private String description;

    /**
     * Optional URL pointing to an icon image for this achievement.
     */
    private String iconUrl;

    /**
     * Category/type of the achievement (XP milestone, streak, etc.).
     */
    private AchievementType type;

    /**
     * Whether the user has unlocked this achievement.
     */
    private boolean unlocked;

    /**
     * When the achievement was earned. Null if not yet unlocked.
     */
    private LocalDateTime earnedAt;

    // Constructors

    public AchievementWithStatusDTO() {
    }

    public AchievementWithStatusDTO(String code,
                                    String title,
                                    String description,
                                    String iconUrl,
                                    AchievementType type,
                                    boolean unlocked,
                                    LocalDateTime earnedAt) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.iconUrl = iconUrl;
        this.type = type;
        this.unlocked = unlocked;
        this.earnedAt = earnedAt;
    }

    // Getters and setters

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

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public AchievementType getType() {
        return type;
    }

    public void setType(AchievementType type) {
        this.type = type;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public LocalDateTime getEarnedAt() {
        return earnedAt;
    }

    public void setEarnedAt(LocalDateTime earnedAt) {
        this.earnedAt = earnedAt;
    }
}
