package com.sep.sep_backend.admin.dto;

import java.util.UUID;

/**
 * Lightweight projection of a user for the admin "users list" view.
 *
 * This DTO is returned by GET /api/admin/users and intentionally only contains
 * fields that are useful in the overview table:
 *  - userId: technical identifier
 *  - username: app-internal name
 *  - email: for contact / identification
 *  - role: USER or ADMIN (from UserRole)
 *  - totalXp: accumulated XP from UserLearning
 *  - streak: current streak count
 */
public class AdminUserSummaryResponse {

    private UUID userId;
    private String username;
    private String email;
    private String role;
    private Integer totalXp;
    private Integer streak;

    public AdminUserSummaryResponse() {
        // Default constructor needed for JSON deserialization / frameworks
    }

    public AdminUserSummaryResponse(
            UUID userId,
            String username,
            String email,
            String role,
            Integer totalXp,
            Integer streak
    ) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.totalXp = totalXp;
        this.streak = streak;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getTotalXp() {
        return totalXp;
    }

    public void setTotalXp(Integer totalXp) {
        this.totalXp = totalXp;
    }

    public Integer getStreak() {
        return streak;
    }

    public void setStreak(Integer streak) {
        this.streak = streak;
    }
}
