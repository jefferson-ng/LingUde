package com.sep.sep_backend.friendship.dto;

import java.util.UUID;

public class UserSummaryDTO {

    private UUID id;
    private String username;
    private String email;

    public UserSummaryDTO(){}

    public UserSummaryDTO(UUID id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
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
}
