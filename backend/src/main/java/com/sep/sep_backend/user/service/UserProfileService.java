package com.sep.sep_backend.user.service;

import com.sep.sep_backend.user.dto.AchievementSummaryDTO;
import com.sep.sep_backend.user.dto.UserProfileResponse;
import com.sep.sep_backend.user.entity.*;
import com.sep.sep_backend.user.repository.AchievementRepository;
import com.sep.sep_backend.user.repository.UserAchievementRepository;
import com.sep.sep_backend.user.repository.UserLearningRepository;
import com.sep.sep_backend.user.repository.UserProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserLearningRepository userLearningRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final AchievementRepository achievementRepository;
    private final UserService userService;


    public UserProfileService(UserProfileRepository userProfileRepository,
                              UserLearningRepository userLearningRepository,
                              UserAchievementRepository userAchievementRepository,
                              AchievementRepository achievementRepository,
                              UserService userService) {
        this.userProfileRepository = userProfileRepository;
        this.userLearningRepository = userLearningRepository;
        this.userAchievementRepository = userAchievementRepository;
        this.achievementRepository = achievementRepository;
        this.userService = userService;
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
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile() {

        // 1. Get the currently authenticated user
        User user = userSegit rvice.getCurrentUser();

        // 2. UserProfile may or may not exist yet
        UserProfile profile = userProfileRepository
                .findByUser(user)
                .orElseGet(() -> new UserProfile(user)); // fallback with empty fields

        // 3. UserLearning must exist (created during signup)
        UserLearning learning = userLearningRepository
                .findByUser(user)
                .orElseThrow(() -> new IllegalStateException("UserLearning not found for user"));

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
}