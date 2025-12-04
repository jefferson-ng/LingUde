package com.sep.sep_backend.user.dto;

import com.sep.sep_backend.user.entity.AchievementType;

/**
 * Simple data transfer object (DTO) that represents
 * a single achievement in the API response.
 * <p>
 * This is what the frontend will receive for each achievement
 * on the profile page (no database or JPA annotations here).
 * </p>
 */
public class AchievementSummaryDTO {

    /**
     * Technical identifier for the achievement, e.g. "FIRST_LESSON".
     * Useful for frontend logic or analytics.
     */
    private String code;

    /**
     * Human-readable title of the achievement, shown to the user.
     * Example: "First Lesson Completed".
     */
    private String title;

    /**
     * Short description explaining why the user got this achievement.
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

    // Constructors

    public AchievementSummaryDTO() {
    }

    public AchievementSummaryDTO(String code,
                                 String title,
                                 String description,
                                 String iconUrl,
                                 AchievementType type) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.iconUrl = iconUrl;
        this.type = type;
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
}
