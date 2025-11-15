package com.sep.sep_backend.user.service;

import com.sep.sep_backend.user.dto.UserSettingsDTO;
import com.sep.sep_backend.user.entity.Language;
import com.sep.sep_backend.user.entity.User;
import com.sep.sep_backend.user.entity.UserSettings;
import com.sep.sep_backend.user.repository.UserRepository;
import com.sep.sep_backend.user.repository.UserSettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UserSettingsService}.
 * <p>
 * These tests verify that the service correctly:
 * <ul>
 *     <li>Retrieves existing settings for a user and maps them to a response DTO.</li>
 *     <li>Updates user settings based on a {@link UserSettingsDTO} and persists the changes.</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class UserSettingsServiceTest {

    @Mock
    private UserSettingsRepository userSettingsRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserSettingsService userSettingsService;

    private User user;
    private UserSettings existingSettings;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());

        existingSettings = new UserSettings(user);
        existingSettings.setUiLanguage(Language.EN);
        existingSettings.setNotificationsEnabled(true);
        existingSettings.setTheme("LIGHT");
    }

    @Test
    void getSettingsForUser_returnsMappedResponse_whenSettingsExist() {
        // arrange
        when(userSettingsRepository.findByUser(user)).thenReturn(Optional.of(existingSettings));

        // act
        UserSettingsDTO response = userSettingsService.getSettingsForUser(user);

        // assert
        assertThat(response.getUiLanguage()).isEqualTo(Language.EN);
        assertThat(response.getNotificationsEnabled()).isTrue();
        assertThat(response.getTheme()).isEqualTo("LIGHT");

        verify(userSettingsRepository).findByUser(user);
        verify(userSettingsRepository, never()).save(any(UserSettings.class));
    }

    @Test
    void updateSettings_updatesFieldsAndSavesEntity() {
        // arrange – existing settings in DB
        when(userSettingsRepository.findByUser(user)).thenReturn(Optional.of(existingSettings));
        when(userSettingsRepository.save(any(UserSettings.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserSettingsDTO request = new UserSettingsDTO();
        request.setUiLanguage(Language.DE);
        request.setNotificationsEnabled(false);
        request.setTheme("DARK");

        // act
        UserSettingsDTO response = userSettingsService.updateSettings(user, request);

        // assert: service returns updated values
        assertThat(response.getUiLanguage()).isEqualTo(Language.DE);
        assertThat(response.getNotificationsEnabled()).isFalse();
        assertThat(response.getTheme()).isEqualTo("DARK");

        // assert: entity passed to repository also has updated values
        verify(userSettingsRepository).save(any(UserSettings.class));
    }
}
