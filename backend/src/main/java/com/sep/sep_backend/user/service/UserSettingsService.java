package com.sep.sep_backend.user.service;

import com.sep.sep_backend.user.dto.UserSettingsDTO;
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


    /**
     * Returns the settings for the given user.
     * <p>
     * If no settings record exists yet, this method automatically creates a
     * new {@link UserSettings} instance with default values and persists it.
     * This guarantees that every user always has a valid settings entry.
     *
     * @param user The user whose settings should be loaded or created.
     * @return An existing or newly created {@link UserSettings} entity.
     */

    public UserSettings getOrCreateSettings(User user) {
        return userSettingsRepository.findByUser(user)
                .orElseGet(() -> userSettingsRepository.save(new UserSettings(user)));
    }

    /**
     * Retrieves the settings for the given user and maps them to a response DTO.
     * <p>
     * Ensures that default settings exist by delegating to {@link #getOrCreateSettings(User)}.
     *
     * @param user The authenticated user.
     * @return A {@link UserSettingsDTO} containing the user's current preferences.
     */

    public UserSettingsDTO getSettingsForUser(User user) {
        UserSettings settings = getOrCreateSettings(user);
        return mapToResponse(settings);
    }

    /**
     * Updates the user's settings using the provided request data.
     * <p>
     * Only non-null fields in the {@link UserSettingsDTO} are updated.
     * The method also ensures that a settings record exists for the user.
     *
     * @param user    The authenticated user whose settings should be modified.
     * @param request The incoming request containing updated settings fields.
     * @return A {@link UserSettingsDTO} with the final stored values.
     */

    public UserSettingsDTO updateSettings(User user, UserSettingsDTO request) {
        UserSettings settings = getOrCreateSettings(user);

        if (request.getUiLanguage() != null) {
            settings.setUiLanguage(request.getUiLanguage());
        }
        if (request.getNotificationsEnabled() != null) {
            settings.setNotificationsEnabled(request.getNotificationsEnabled());
        }
        if (request.getTheme() != null) {
            settings.setTheme(request.getTheme());
        }

        UserSettings saved = userSettingsRepository.save(settings);
        return mapToResponse(saved);
    }

    /**
     * Converts a {@link UserSettings} entity to a {@link UserSettingsDTO} DTO.
     *
     * @param settings The entity to convert.
     * @return A response DTO containing the same values.
     */


    private UserSettingsDTO mapToResponse(UserSettings settings) {
        return new UserSettingsDTO(
                settings.getUiLanguage(),
                settings.getNotificationsEnabled(),
                settings.getTheme()
        );
    }

}