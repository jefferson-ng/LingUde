package com.sep.sep_backend.user.service;

import com.sep.sep_backend.user.entity.User;
import com.sep.sep_backend.user.entity.UserProfile;
import com.sep.sep_backend.user.repository.UserProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
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