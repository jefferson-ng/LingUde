package com.sep.sep_backend.friendship.dto;

import java.util.UUID;

public class UserSummaryDTO {

    private UUID id;
    private String username;
    private String email;
    private String avatarUrl;

    public UserSummaryDTO(){}

    public UserSummaryDTO(UUID id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.avatarUrl = null;
    }

    public UserSummaryDTO(UUID id, String username, String email, String avatarUrl) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.avatarUrl = avatarUrl;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
