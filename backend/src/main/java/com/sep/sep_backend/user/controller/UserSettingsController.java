package com.sep.sep_backend.user.controller;


import com.sep.sep_backend.auth.JwtUtil;
import com.sep.sep_backend.user.dto.UserSettingsDTO;
import com.sep.sep_backend.user.entity.User;
import com.sep.sep_backend.user.service.UserService;
import com.sep.sep_backend.user.service.UserSettingsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller that exposes endpoints for reading and updating the
 * authenticated user's application settings (language, theme, notifications).
 */
@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = "http://localhost:4200")
public class UserSettingsController {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final UserSettingsService userSettingsService;

    public UserSettingsController(JwtUtil jwtUtil,
                                  UserService userService,
                                  UserSettingsService userSettingsService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.userSettingsService = userSettingsService;
    }

    /**
     * Extract the authenticated user from the JWT token in the request header.
     *
     * @param request the incoming HTTP request containing "Authorization: Bearer <token>"
     * @return the authenticated User
     */
    private User getAuthenticatedUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header.");
        }

        String token = authHeader.substring(7);
        UUID userId = jwtUtil.getUserIdFromToken(token);

        return userService.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Returns the current user's settings.
     */
    @GetMapping
    public ResponseEntity<UserSettingsDTO> getSettings(HttpServletRequest request) {
        User user = getAuthenticatedUser(request);
        UserSettingsDTO response = userSettingsService.getSettingsForUser(user);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates the authenticated user's settings.
     */
    @PutMapping
    public ResponseEntity<UserSettingsDTO> updateSettings(
            HttpServletRequest request,
            @RequestBody UserSettingsDTO settingsRequest) {

        User user = getAuthenticatedUser(request);
        UserSettingsDTO updated = userSettingsService.updateSettings(user, settingsRequest);

        return ResponseEntity.ok(updated);
    }
}
