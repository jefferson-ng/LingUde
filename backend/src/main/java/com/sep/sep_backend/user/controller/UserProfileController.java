package com.sep.sep_backend.user.controller;

import com.sep.sep_backend.user.dto.UserProfileResponse;
import com.sep.sep_backend.user.service.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for profile-related endpoints.
 * <p>
 * Exposes the API used by the frontend to display the learner's
 * profile page: username, avatar, XP, level, and achievements.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    /**
     * Creates a new UserProfileController with the required service dependency.
     *
     * @param userProfileService service providing profile & gamification data
     */
    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    /**
     * Returns the profile information for the currently authenticated user.
     * <p>
     * This endpoint is intended for the profile page on the frontend.
     * It combines:
     * <ul>
     *     <li>Username and display name</li>
     *     <li>Avatar URL</li>
     *     <li>Total XP and current language level</li>
     *     <li>List of unlocked achievements</li>
     * </ul>
     * </p>
     *
     * @return HTTP 200 OK with {@link UserProfileResponse} in the body
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile() {
        UserProfileResponse response = userProfileService.getCurrentUserProfile();
        return ResponseEntity.ok(response);
    }
}
