package com.sep.sep_backend.user.service;

import com.sep.sep_backend.user.entity.Language;
import com.sep.sep_backend.user.entity.User;
import com.sep.sep_backend.user.entity.UserSettings;
import com.sep.sep_backend.user.repository.UserSettingsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserSettingsService {

    private final UserSettingsRepository userSettingsRepository;

    public UserSettingsService(UserSettingsRepository userSettingsRepository) {
        this.userSettingsRepository = userSettingsRepository;
    }

    /**
     * Create new user settings
     * @param userSettings the user settings to create
     * @return the created user settings
     */
    public UserSettings createUserSettings(UserSettings userSettings) {
        return userSettingsRepository.save(userSettings);
    }

    /**
     * Find user settings by ID
     * @param id the settings ID
     * @return Optional containing the user settings if found
     */
    public Optional<UserSettings> findSettingsById(UUID id) {
        return userSettingsRepository.findById(id);
    }

    /**
     * Find user settings by user
     * @param user the user
     * @return Optional containing the user settings if found
     */
    public Optional<UserSettings> findSettingsByUser(User user) {
        return userSettingsRepository.findByUser(user);
    }

    /**
     * Find user settings by user ID
     * @param userId the user ID
     * @return Optional containing the user settings if found
     */
    public Optional<UserSettings> findSettingsByUserId(UUID userId) {
        return userSettingsRepository.findByUserId(userId);
    }

    /**
     * Update existing user settings
     * @param userSettings the user settings with updated information
     * @return the updated user settings
     */
    public UserSettings updateUserSettings(UserSettings userSettings) {
        return userSettingsRepository.save(userSettings);
    }

    /**
     * Update UI language preference
     * @param userId the user ID
     * @param language the new UI language
     * @return Optional containing the updated settings if found
     */
    public Optional<UserSettings> updateUiLanguage(UUID userId, Language language) {
        Optional<UserSettings> settingsOptional = userSettingsRepository.findByUserId(userId);
        if (settingsOptional.isPresent()) {
            UserSettings settings = settingsOptional.get();
            settings.setUiLanguage(language);
            return Optional.of(userSettingsRepository.save(settings));
        }
        return Optional.empty();
    }

    /**
     * Update theme preference
     * @param userId the user ID
     * @param theme the new theme (LIGHT, DARK, AUTO)
     * @return Optional containing the updated settings if found
     */
    public Optional<UserSettings> updateTheme(UUID userId, String theme) {
        Optional<UserSettings> settingsOptional = userSettingsRepository.findByUserId(userId);
        if (settingsOptional.isPresent()) {
            UserSettings settings = settingsOptional.get();
            settings.setTheme(theme);
            return Optional.of(userSettingsRepository.save(settings));
        }
        return Optional.empty();
    }

    /**
     * Update notifications preference
     * @param userId the user ID
     * @param enabled whether notifications are enabled
     * @return Optional containing the updated settings if found
     */
    public Optional<UserSettings> updateNotificationsEnabled(UUID userId, Boolean enabled) {
        Optional<UserSettings> settingsOptional = userSettingsRepository.findByUserId(userId);
        if (settingsOptional.isPresent()) {
            UserSettings settings = settingsOptional.get();
            settings.setNotificationsEnabled(enabled);
            return Optional.of(userSettingsRepository.save(settings));
        }
        return Optional.empty();
    }

    /**
     * Delete user settings by ID
     * @param id the settings ID
     */
    public void deleteUserSettings(UUID id) {
        userSettingsRepository.deleteById(id);
    }

    /**
     * Delete user settings by user
     * @param user the user whose settings should be deleted
     */
    public void deleteUserSettingsByUser(User user) {
        userSettingsRepository.deleteByUser(user);
    }
}