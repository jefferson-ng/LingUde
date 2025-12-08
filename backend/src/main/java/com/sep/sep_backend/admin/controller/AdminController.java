package com.sep.sep_backend.admin.controller;

import com.sep.sep_backend.user.entity.User;
import com.sep.sep_backend.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Simple admin controller to verify that admin authentication
 * and authorization work correctly.
 *
 * <p>
 * All endpoints in this controller are protected by Spring Security
 * configuration and require the caller to have role {@code ADMIN}.
 * </p>
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Returns basic information about the currently authenticated admin.
     *
     * <p>
     * This endpoint is mainly intended for testing from Postman or the
     * admin frontend. It allows us to verify that:
     * <ul>
     *     <li>the JWT is valid</li>
     *     <li>the role claim is correctly interpreted as {@code ADMIN}</li>
     *     <li>Spring Security blocks non-admin users with HTTP 403</li>
     * </ul>
     * </p>
     *
     * @return JSON with userId, username, email and role of the current admin
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentAdmin() {
        User currentUser = userService.getCurrentUser();

        Map<String, Object> body = Map.of(
                "userId", currentUser.getId(),
                "username", currentUser.getUsername(),
                "email", currentUser.getEmail(),
                "role", currentUser.getRole().name(),
                "message", "You are an authenticated ADMIN 🎉"
        );

        return ResponseEntity.ok(body);
    }
}
