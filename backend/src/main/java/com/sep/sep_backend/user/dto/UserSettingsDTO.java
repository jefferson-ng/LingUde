package com.sep.sep_backend.user.dto;

import com.sep.sep_backend.user.entity.Language;

/**
 * Single DTO used for both sending and receiving user settings data.
 *
 * This class is used as:
 *  - request body when the client updates the settings
 *  - response body when the backend returns the current settings
 *
 * Fields:
 *  uiLanguage           – preferred interface language
 *  notificationsEnabled – whether notifications are enabled
 *  theme                – UI theme (e.g. "LIGHT", "DARK", "AUTO")
 */
public class UserSettingsDTO {

    private Language uiLanguage;
    private Boolean notificationsEnabled;
    private String theme;

    public UserSettingsDTO() {
    }

    public UserSettingsDTO(Language uiLanguage, Boolean notificationsEnabled, String theme) {
        this.uiLanguage = uiLanguage;
        this.notificationsEnabled = notificationsEnabled;
        this.theme = theme;
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
