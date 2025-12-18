package com.sep.sep_backend.admin.service;

import com.sep.sep_backend.admin.dto.AdminUserAchievementDTO;
import com.sep.sep_backend.admin.dto.AdminUserDetailResponse;
import com.sep.sep_backend.admin.dto.AdminUserSummaryResponse;
import com.sep.sep_backend.user.entity.*;
import com.sep.sep_backend.user.repository.UserAchievementRepository;
import com.sep.sep_backend.user.repository.UserLearningRepository;
import com.sep.sep_backend.user.repository.UserProfileRepository;
import com.sep.sep_backend.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * AdminUserService
 *
 * <p>
 *     This service contains all backend logic for the admin feature
 *     "view users in the admin panel".
 * </p>
 *
 * Responsibilities:
 * <ul>
 *     <li>Return a paginated list of users with basic learning stats</li>
 *     <li>Return a full detailed view for a single user</li>
 *     <li>Ensure that only existing users are returned (404 for unknown IDs)</li>
 * </ul>
 */
@Service
@Transactional(readOnly = true)
public class AdminUserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserLearningRepository userLearningRepository;
    private final UserAchievementRepository userAchievementRepository;

    public AdminUserService(UserRepository userRepository,
                            UserProfileRepository userProfileRepository,
                            UserLearningRepository userLearningRepository,
                            UserAchievementRepository userAchievementRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.userLearningRepository = userLearningRepository;
        this.userAchievementRepository = userAchievementRepository;
    }

    /**
     * Returns a paginated list of users for the admin panel.
     *
     * Each entry contains:
     * - userId
     * - username
     * - email
     * - role
     * - totalXp
     * - streak
     *
     * @param page zero-based page index
     * @param size page size
     * @return Page of AdminUserSummaryResponse
     */
    public Page<AdminUserSummaryResponse> getUsers(int page, int size) {
        // Create a Pageable with page & size from the controller
        Pageable pageable = PageRequest.of(page, size);

        // Fetch users page from DB
        Page<User> usersPage = userRepository.findAll(pageable);

        // Map each User entity to our admin summary DTO
        return usersPage.map(this::mapToSummary);
    }

    /**
     * Returns a full detailed view for a single user.
     *
     * This includes:
     * - basic user info
     * - profile data (displayName, avatarUrl, bio, city, country)
     * - learning info (language, levels, XP, streak, last activity)
     * - achievements (code, title, description, earnedAt)
     *
     * @param userId ID of the user to load
     * @return AdminUserDetailResponse with all information
     * @throws ResponseStatusException (404) when the user does not exist
     */
    public AdminUserDetailResponse getUserDetails(UUID userId) {
        // 1) Load the user or return 404 if not found
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // 2) Load optional profile & learning data
        UserProfile profile = userProfileRepository
                .findByUserId(userId)
                .orElse(null);

        UserLearning learning = userLearningRepository
                .findByUser_Id(userId)
                .orElse(null);

        // 3) Load all achievements for this user
        List<UserAchievement> userAchievements =
                userAchievementRepository.findByUserId(userId);

        List<AdminUserAchievementDTO> achievementDTOs = new ArrayList<>();
        for (UserAchievement ua : userAchievements) {
            Achievement a = ua.getAchievement();
            achievementDTOs.add(new AdminUserAchievementDTO(
                    a.getCode(),
                    a.getTitle(),
                    a.getDescription(),
                    ua.getEarnedAt()
            ));
        }

        // 4) Build the detailed response DTO
        return new AdminUserDetailResponse(
                // Basic info (never null)
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole() != null ? user.getRole().name() : null,
                // Profile info (may be null -> handle with null checks)
                profile != null ? profile.getDisplayName() : null,
                profile != null ? profile.getAvatarUrl() : null,
                profile != null ? profile.getBio() : null,
                profile != null ? profile.getCity() : null,
                profile != null ? profile.getCountry() : null,
                // Learning info (may be null if user never started learning)
                learning != null && learning.getLearningLanguage() != null ? learning.getLearningLanguage().name() : null,
                learning != null && learning.getCurrentLevel() != null ? learning.getCurrentLevel().name() : null,
                learning != null && learning.getTargetLevel() != null ? learning.getTargetLevel().name() : null,
                learning != null ? learning.getXp() : 0,
                learning != null ? learning.getStreakCount() : 0,
                learning != null ? learning.getLastActivityDate() : null,
                // Achievements list (possibly empty)
                achievementDTOs
        );
    }
    @Transactional(readOnly = false)
    public AdminUserSummaryResponse updateUserRole(UUID userId, UserRole newRole) {

        // 1) Load current admin (for self-change protection)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UUID currentAdminId = UUID.fromString(auth.getName());

        User currentAdmin = userRepository.findById(currentAdminId)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in DB: " + currentAdminId));


        // 2) Prevent admin from changing their own role
        if (currentAdmin.getId().equals(userId)) {
            throw new IllegalArgumentException("You cannot change your own role.");
        }

        // 3) Load target user
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // 4) Update + persist
        targetUser.setRole(newRole);
        User saved = userRepository.save(targetUser);

        // 5) Return updated summary (reuse your existing summary mapping method)
        return mapToSummary(saved);
    }


    /**
     * Helper method:
     * Maps a User entity to the admin summary DTO used in the list endpoint.
     *
     * @param user entity from the database
     * @return AdminUserSummaryResponse with basic info + learning stats
     */
    private AdminUserSummaryResponse mapToSummary(User user) {
        // Try to load learning stats; if not present, use sensible defaults
        UserLearning learning = userLearningRepository
                .findByUser_Id(user.getId())
                .orElse(null);

        int totalXp = learning != null ? learning.getXp() : 0;
        int streak = learning != null ? learning.getStreakCount() : 0;

        return new AdminUserSummaryResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole() != null ? user.getRole().name() : null,
                totalXp,
                streak
        );
    }
}
