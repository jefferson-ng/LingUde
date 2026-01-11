package com.sep.sep_backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * ForgotPasswordRequest
 *
 * Sent by the client when the user clicks "Forgot password" and enters their email.
 *
 * Example JSON:
 * {
 *   "email": "user@example.com"
 * }
 */
public class ForgotPasswordRequest {

    @NotBlank
    @Email
    private String email;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

