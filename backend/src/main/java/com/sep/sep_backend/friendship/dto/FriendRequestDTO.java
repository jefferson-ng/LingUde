package com.sep.sep_backend.friendship.dto;

import jakarta.validation.constraints.NotBlank;

public class FriendRequestDTO {

    @NotBlank(message = "Username or email is required")
    private String usernameOrEmail;

    public FriendRequestDTO(){}

    public FriendRequestDTO(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }
}
