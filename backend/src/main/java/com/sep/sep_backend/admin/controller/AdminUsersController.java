package com.sep.sep_backend.admin.controller;

import com.sep.sep_backend.admin.dto.AdminUserDetailResponse;
import com.sep.sep_backend.admin.dto.AdminUserSummaryResponse;
import com.sep.sep_backend.admin.service.AdminUserService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import com.sep.sep_backend.admin.dto.AdminUpdateUserRoleRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.UUID;

/**
 * AdminUsersController
 *
 * Exposes admin-only endpoints for viewing users.
 * These routes must be protected by Spring Security using /api/admin/** rules.
 */
@RestController
@RequestMapping("/api/admin/users")
public class AdminUsersController {

    private final AdminUserService adminUserService;

    public AdminUsersController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    /**
     * GET /api/admin/users?page=0&size=20
     *
     * Returns a paginated list of users (summary view).
     */
    @GetMapping
    public Page<AdminUserSummaryResponse> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return adminUserService.getUsers(page, size);
    }

    /**
     * GET /api/admin/users/{userId}
     *
     * Returns a detailed view for one user.
     */
    @GetMapping("/{userId}")
    public AdminUserDetailResponse getUserDetails(@PathVariable UUID userId) {
        return adminUserService.getUserDetails(userId);
    }
    @PatchMapping("/{userId}/role")
    public AdminUserSummaryResponse updateUserRole(
            @PathVariable UUID userId,
            @Valid @RequestBody AdminUpdateUserRoleRequest request
    ) {
        return adminUserService.updateUserRole(userId, request.getRole());
    }


}
