package com.sep.sep_backend.user.dto;

import java.util.UUID;

public class AuthResponse {
    private UUID userId;
    private String email;
    private String name;


    public AuthResponse(UUID userId, String email, String userame) {
        this.userId = userId;
        this.email = email;
        this.name = userame;

    }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }


}
