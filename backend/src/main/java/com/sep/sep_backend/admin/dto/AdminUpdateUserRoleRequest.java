package com.sep.sep_backend.admin.dto;

import com.sep.sep_backend.user.entity.UserRole;
import jakarta.validation.constraints.NotNull;

public class AdminUpdateUserRoleRequest {

    @NotNull
    private UserRole role;

    public AdminUpdateUserRoleRequest() {
    }

    public AdminUpdateUserRoleRequest(UserRole role) {
        this.role = role;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}

