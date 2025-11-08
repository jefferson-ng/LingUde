package com.sep.sep_backend.user.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "user_settings")
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "ui_language", length = 10)
    private Language uiLanguage = Language.EN;

    @Column(name = "notifications_enabled", nullable = false)
    private Boolean notificationsEnabled = true;

    @Column(length = 10)
    private String theme = "AUTO";

    // Constructors
    public UserSettings() {
    }

    public UserSettings(User user) {
        this.user = user;
        this.notificationsEnabled = true;
        this.theme = "AUTO";
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

    public Language getUiLanguage() {
        return uiLanguage;
    }

    public void setUiLanguage(Language uiLanguage) {
        this.uiLanguage = uiLanguage;
    }

    public Boolean getNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(Boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}