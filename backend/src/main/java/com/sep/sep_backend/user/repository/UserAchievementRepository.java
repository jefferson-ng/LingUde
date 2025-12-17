package com.sep.sep_backend.user.repository;

import com.sep.sep_backend.user.entity.Achievement;
import com.sep.sep_backend.user.entity.User;
import com.sep.sep_backend.user.entity.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing {@link UserAchievement} entities.
 * <p>
 * Links users to the achievements they have earned.
 * Typical use cases:
 * </p>
 * <ul>
 *     <li>Load all achievements for a given user.</li>
 *     <li>Check if a user already has a specific achievement.</li>
 * </ul>
 */
public interface UserAchievementRepository extends JpaRepository<UserAchievement, UUID> {

    /**
     * Returns all {@link UserAchievement} entries for the given user.
     *
     * @param user the user whose achievements should be loaded
     * @return list of UserAchievement entries for that user
     */
    List<UserAchievement> findByUser(User user);

    List<UserAchievement> findByUserId(UUID userId);

    /**
     * Checks whether the given user already has the specified achievement.
     *
     * @param user        the user to check
     * @param achievement the achievement to check
     * @return true if a UserAchievement entry exists, false otherwise
     */
    boolean existsByUserAndAchievement(User user, Achievement achievement);
}
