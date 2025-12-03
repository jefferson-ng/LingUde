package com.sep.sep_backend.user.entity;

import jakarta.persistence.*;

import java.util.UUID;

/**
 * Master data for all possible achievements in the system.
 */
@Entity
@Table(name = "achievements")
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String code; // e.g. "FIRST_LESSON", "XP_100"

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(name = "icon_url", length = 255)
    private String iconUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AchievementType type;

    // Constructors

    public Achievement() {
    }

    public Achievement(String code,
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
