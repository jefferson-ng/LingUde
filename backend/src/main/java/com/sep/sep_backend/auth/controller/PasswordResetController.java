package com.sep.sep_backend.auth.controller;

import com.sep.sep_backend.auth.dto.ForgotPasswordRequest;
import com.sep.sep_backend.auth.dto.ResetPasswordRequest;
import com.sep.sep_backend.auth.service.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * PasswordResetController
 *
 * REST API endpoints for password reset functionality.
 *
 * Important security principle:
 * - /forgot-password must NOT reveal if an email exists in the system.
 *   Therefore it ALWAYS returns the same success message.
 *
 * This controller is placed under /api/auth so it is public (permitAll),
 * just like signin/signup. SecurityConfig already allows /api/auth/**. :contentReference[oaicite:1]{index=1}
 */
@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    /**
     * Request a password reset email.
     *
     * Example:
     * POST /api/auth/forgot-password
     * {
     *   "email": "user@example.com"
     * }
     *
     * Response is ALWAYS 200 OK, even if the email does not exist.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {
        passwordResetService.requestPasswordReset(request.getEmail());

        // Always return a generic message to prevent user enumeration.
        return ResponseEntity.ok(Map.of(
                "message", "If this email exists, a password reset link has been sent."
        ));
    }

    /**
     * Reset password using token + new password.
     *
     * Example:
     * POST /api/auth/reset-password
     * {
     *   "token": "RAW_TOKEN_FROM_EMAIL",
     *   "newPassword": "NewPassword123"
     * }
     *
     * If token is invalid/expired/used, an exception is thrown.
     * We'll map these exceptions in GlobalExceptionHandler in the next step.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());

        return ResponseEntity.ok(Map.of(
                "message", "Password reset successful."
        ));
    }
}
