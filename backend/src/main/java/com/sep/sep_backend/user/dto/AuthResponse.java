package com.sep.sep_backend.user.dto;

import java.util.UUID;

public class AuthResponse {
    private UUID userId;
    private String email;
    private String name;
    private String accessToken;
    private String refreshToken;

    public AuthResponse(UUID userId, String email, String username, String accessToken, String refreshToken) {
        this.userId = userId;
        this.email = email;
        this.name = username;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    // Getters and Setters
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}
