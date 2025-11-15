package com.sep.sep_backend.user.controller;

import com.sep.sep_backend.auth.JwtUtil;
import com.sep.sep_backend.user.dto.UserSettingsDTO;
import com.sep.sep_backend.user.entity.Language;
import com.sep.sep_backend.user.entity.User;
import com.sep.sep_backend.user.service.UserService;
import com.sep.sep_backend.user.service.UserSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UserSettingsController}.
 * <p>
 * These tests verify that the controller:
 * <ul>
 *     <li>Extracts the authenticated user from the JWT token.</li>
 *     <li>Delegates to {@link UserSettingsService} to fetch and update settings.</li>
 *     <li>Returns the expected HTTP responses.</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class UserSettingsControllerTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @Mock
    private UserSettingsService userSettingsService;

    @InjectMocks
    private UserSettingsController userSettingsController;

    private UUID userId;
    private User user;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);

        request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer test-token");
    }

    @Test
    void getSettings_returnsSettingsForAuthenticatedUser() {
        // arrange
        UserSettingsDTO responseDto =
                new UserSettingsDTO(Language.EN, true, "LIGHT");

        when(jwtUtil.getUserIdFromToken("test-token")).thenReturn(userId);
        when(userService.findUserById(userId)).thenReturn(Optional.of(user));
        when(userSettingsService.getSettingsForUser(user)).thenReturn(responseDto);

        // act
        ResponseEntity<UserSettingsDTO> response = userSettingsController.getSettings(request);

        // assert
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUiLanguage()).isEqualTo(Language.EN);
        assertThat(response.getBody().getNotificationsEnabled()).isTrue();
        assertThat(response.getBody().getTheme()).isEqualTo("LIGHT");

        verify(jwtUtil).getUserIdFromToken("test-token");
        verify(userService).findUserById(userId);
        verify(userSettingsService).getSettingsForUser(user);
    }

    @Test
    void updateSettings_updatesSettingsForAuthenticatedUser() {
        // arrange
        UserSettingsDTO requestDto = new UserSettingsDTO();
        requestDto.setUiLanguage(Language.DE);
        requestDto.setNotificationsEnabled(false);
        requestDto.setTheme("DARK");

        UserSettingsDTO updatedDto =
                new UserSettingsDTO(Language.DE, false, "DARK");

        when(jwtUtil.getUserIdFromToken("test-token")).thenReturn(userId);
        when(userService.findUserById(userId)).thenReturn(Optional.of(user));
        when(userSettingsService.updateSettings(any(User.class), any(UserSettingsDTO.class)))
                .thenReturn(updatedDto);

        // act
        ResponseEntity<UserSettingsDTO> response =
                userSettingsController.updateSettings(request, requestDto);

        // assert
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUiLanguage()).isEqualTo(Language.DE);
        assertThat(response.getBody().getNotificationsEnabled()).isFalse();
        assertThat(response.getBody().getTheme()).isEqualTo("DARK");

        verify(jwtUtil).getUserIdFromToken("test-token");
        verify(userService).findUserById(userId);
        verify(userSettingsService).updateSettings(user, requestDto);
    }
}
