package com.sep.sep_backend.user.repository;

import com.sep.sep_backend.user.entity.User;
import com.sep.sep_backend.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    /**
     * Find user profiles for multiple users by their IDs (batch query)
     * Used for leaderboard queries to avoid N+1 problem
     * @param userIds list of user IDs
     * @return List of user profiles for the specified users
     */
    @Query("SELECT up FROM UserProfile up WHERE up.user.id IN :userIds")
    List<UserProfile> findByUser_IdIn(@Param("userIds") List<UUID> userIds);
}
