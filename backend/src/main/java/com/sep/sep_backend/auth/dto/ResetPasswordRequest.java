package com.sep.sep_backend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * ResetPasswordRequest
 *
 * Sent by the client when the user submits the reset token and a new password.
 *
 * Example JSON:
 * {
 *   "token": "RAW_TOKEN_FROM_EMAIL",
 *   "newPassword": "MyNewPassword123"
 * }
 */
public class ResetPasswordRequest {

    /**
     * Raw reset token from the email link.
     * We will hash it in the backend and compare with DB.
     */
    @NotBlank
    private String token;

    /**
     * New password chosen by the user.
     * We follow the same minimum length rule as SignupRequest (min 8).
     */
    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String newPassword;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
