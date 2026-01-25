package com.sep.sep_backend.user.service;

import com.sep.sep_backend.user.dto.AchievementSummaryDTO;
import com.sep.sep_backend.user.dto.AchievementWithStatusDTO;
import com.sep.sep_backend.user.dto.UserProfileResponse;
import com.sep.sep_backend.user.entity.*;
import com.sep.sep_backend.user.repository.AchievementRepository;
import com.sep.sep_backend.user.repository.UserAchievementRepository;
import com.sep.sep_backend.user.repository.UserLearningRepository;
import com.sep.sep_backend.user.repository.UserProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserLearningRepository userLearningRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final AchievementRepository achievementRepository;
    private final UserService userService;
    private final AchievementService achievementService;


    public UserProfileService(UserProfileRepository userProfileRepository,
                              UserLearningRepository userLearningRepository,
                              UserAchievementRepository userAchievementRepository,
                              AchievementRepository achievementRepository,
                              UserService userService,
                              AchievementService achievementService) {
        this.userProfileRepository = userProfileRepository;
        this.userLearningRepository = userLearningRepository;
        this.userAchievementRepository = userAchievementRepository;
        this.achievementRepository = achievementRepository;
        this.userService = userService;
        this.achievementService = achievementService;
    }


    /**
     * Create a new user profile
     * @param userProfile the user profile to create
     * @return the created user profile
     */
    public UserProfile createUserProfile(UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }

    /**
     * Find a user profile by ID
     * @param id the profile ID
     * @return Optional containing the user profile if found
     */
    public Optional<UserProfile> findProfileById(UUID id) {
        return userProfileRepository.findById(id);
    }

    /**
     * Find a user profile by user
     * @param user the user
     * @return Optional containing the user profile if found
     */
    public Optional<UserProfile> findProfileByUser(User user) {
        return userProfileRepository.findByUser(user);
    }

    /**
     * Find a user profile by user ID
     * @param userId the user ID
     * @return Optional containing the user profile if found
     */
    public Optional<UserProfile> findProfileByUserId(UUID userId) {
        return userProfileRepository.findByUserId(userId);
    }

    /**
     * Update an existing user profile
     * @param userProfile the user profile with updated information
     * @return the updated user profile
     */
    public UserProfile updateUserProfile(UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }

    /**
     * Update specific profile fields
     * @param userId the user ID
     * @param displayName the new display name (optional)
     * @param bio the new bio (optional)
     * @param city the new city (optional)
     * @param country the new country (optional)
     * @param avatarUrl the new avatar URL (optional)
     * @return Optional containing the updated profile if found
     */
    public Optional<UserProfile> updateProfileFields(UUID userId, String displayName, String bio,
                                                      String city, String country, String avatarUrl) {
        Optional<UserProfile> profileOptional = userProfileRepository.findByUserId(userId);
        if (profileOptional.isPresent()) {
            UserProfile profile = profileOptional.get();
            if (displayName != null) profile.setDisplayName(displayName);
            if (bio != null) profile.setBio(bio);
            if (city != null) profile.setCity(city);
            if (country != null) profile.setCountry(country);
            if (avatarUrl != null) profile.setAvatarUrl(avatarUrl);
            return Optional.of(userProfileRepository.save(profile));
        }
        return Optional.empty();
    }
    /**
     * Builds and returns the complete profile response for the currently logged-in user.
     * <p>
     * This method collects data from:
     * <ul>
     *     <li>The User entity (username)</li>
     *     <li>UserProfile (display name & avatar)</li>
     *     <li>UserLearning (XP and language level)</li>
     *     <li>UserAchievement (list of earned achievements)</li>
     * </ul>
     * Then it maps them into a {@link com.sep.sep_backend.user.dto.UserProfileResponse}
     * which is returned to the controller and sent to the frontend.
     * </p>
     *
     * @return UserProfileResponse containing profile & gamification data
     */
    @Transactional
    public UserProfileResponse getCurrentUserProfile() {

        // 1. Get the currently authenticated user
        User user = userService.getCurrentUser();

        // 2. Ensure UserProfile exists (create if missing)
        UserProfile profile = userProfileRepository
                .findByUser(user)
                .orElseGet(() -> {
                    UserProfile p = new UserProfile(user);
                    return userProfileRepository.save(p);
                });

        // 3. Ensure UserLearning exists (create if missing)
        UserLearning learning = userLearningRepository
                .findByUser(user)
                .orElseGet(() -> {
                    UserLearning l = new UserLearning(user);
                    return userLearningRepository.save(l);
                });

        // 4. Fetch all achievements earned by this user
        List<UserAchievement> earned = userAchievementRepository.findByUser(user);

        // 5. Map achievements to DTOs
        List<AchievementSummaryDTO> achievementDTOs = earned.stream()
                .map(ua -> {
                    Achievement a = ua.getAchievement();
                    return new AchievementSummaryDTO(
                            a.getCode(),
                            a.getTitle(),
                            a.getDescription(),
                            a.getIconUrl(),
                            a.getType()
                    );
                })
                .toList();

        // 6. Build and return the full API response
        return new UserProfileResponse(
                user.getUsername(),
                profile.getDisplayName(),
                profile.getAvatarUrl(),
                learning.getXp(),
                learning.getCurrentLevel(),
                achievementDTOs
        );
    }

    /**
     * Returns all achievements with their unlock status for the current user.
     * <p>
     * This method fetches ALL achievements from the catalog and indicates
     * which ones the user has already earned. Before returning, it also
     * checks if any achievements should be unlocked based on current progress.
     * Unlocked achievements are sorted first, then by type.
     * </p>
     *
     * @return list of all achievements with unlock status
     */
    @Transactional
    public List<AchievementWithStatusDTO> getAllAchievementsWithStatus() {

        // 1. Get the currently authenticated user
        User user = userService.getCurrentUser();

        // 2. Check and grant any missing achievements based on current progress
        grantMissingAchievements(user);

        // 3. Fetch all achievements from the catalog
        List<Achievement> allAchievements = achievementRepository.findAll();

        // 4. Fetch user's earned achievements and create a map for O(1) lookup
        List<UserAchievement> userAchievements = userAchievementRepository.findByUser(user);
        Map<UUID, LocalDateTime> earnedMap = userAchievements.stream()
                .collect(Collectors.toMap(
                        ua -> ua.getAchievement().getId(),
                        UserAchievement::getEarnedAt
                ));

        // 5. Map to DTOs with unlock status
        return allAchievements.stream()
                .map(a -> {
                    boolean isUnlocked = earnedMap.containsKey(a.getId());
                    LocalDateTime earnedAt = earnedMap.get(a.getId());
                    return new AchievementWithStatusDTO(
                            a.getCode(),
                            a.getTitle(),
                            a.getDescription(),
                            a.getIconUrl(),
                            a.getType(),
                            isUnlocked,
                            earnedAt
                    );
                })
                .sorted((a1, a2) -> {
                    // Sort: unlocked first, then by type
                    if (a1.isUnlocked() != a2.isUnlocked()) {
                        return a1.isUnlocked() ? -1 : 1;
                    }
                    return a1.getType().compareTo(a2.getType());
                })
                .toList();
    }

    /**
     * Checks the user's current progress and grants any achievements
     * they've earned but haven't received yet.
     */
    private void grantMissingAchievements(User user) {
        // Get user's learning data
        Optional<UserLearning> learningOpt = userLearningRepository.findByUser(user);
        if (learningOpt.isEmpty()) {
            return;
        }

        UserLearning learning = learningOpt.get();
        int xp = learning.getXp() != null ? learning.getXp() : 0;
        int streak = learning.getStreakCount() != null ? learning.getStreakCount() : 0;

        // Check XP milestones
        if (xp >= 100) {
            achievementService.grantAchievementIfNotOwned(user, "XP_100");
        }
        if (xp >= 600) {
            achievementService.grantAchievementIfNotOwned(user, "XP_600");
        }

        // Check Streak milestones
        if (streak >= 3) {
            achievementService.grantAchievementIfNotOwned(user, "STREAK_3");
        }
        if (streak >= 7) {
            achievementService.grantAchievementIfNotOwned(user, "STREAK_7");
        }
    }

    /**
     * Delete a user profile by ID
     * @param id the profile ID
     */
    public void deleteUserProfile(UUID id) {
        userProfileRepository.deleteById(id);
    }

    /**
     * Delete a user profile by user
     * @param user the user whose profile should be deleted
     */
    public void deleteUserProfileByUser(User user) {
        userProfileRepository.deleteByUser(user);
    }

    /**
     * Updates the avatar URL for the currently authenticated user.
     * Creates a new profile if one doesn't exist.
     *
     * @param avatarUrl the new avatar URL (can be null to remove avatar)
     */
    public void updateAvatar(String avatarUrl) {
        User user = userService.getCurrentUser();
        UserProfile profile = userProfileRepository
                .findByUser(user)
                .orElseGet(() -> {
                    UserProfile p = new UserProfile(user);
                    return userProfileRepository.save(p);
                });
        profile.setAvatarUrl(avatarUrl);
        userProfileRepository.save(profile);
    }
}