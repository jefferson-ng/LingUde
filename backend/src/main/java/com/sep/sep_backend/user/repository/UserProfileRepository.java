package com.sep.sep_backend.user.repository;

import com.sep.sep_backend.user.entity.User;
import com.sep.sep_backend.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

    /**
     * Find a user profile by the associated user
     * @param user the user to search for
     * @return Optional containing the user profile if found
     */
    Optional<UserProfile> findByUser(User user);

    /**
     * Find a user profile by user ID
     * @param userId the user ID to search for
     * @return Optional containing the user profile if found
     */
    Optional<UserProfile> findByUserId(UUID userId);

    /**
     * Delete a user profile by the associated user
     * @param user the user whose profile should be deleted
     */
    void deleteByUser(User user);
}
