package com.sep.sep_backend.user.repository;

import com.sep.sep_backend.user.entity.User;
import com.sep.sep_backend.user.entity.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for managing {@link UserAchievement} entities.
 * <p>
 * This links users to the achievements they have earned.
 * Typical use cases:
 * </p>
 * <ul>
 *     <li>Find all achievements earned by a specific user.</li>
 *     <li>Check if a user already has a certain achievement (custom method later).</li>
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
}
