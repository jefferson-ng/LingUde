package com.sep.sep_backend.user.repository;

import com.sep.sep_backend.user.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing {@link Achievement} entities.
 * <p>
 * This provides CRUD operations for the achievements catalog:
 * creating, reading, updating and deleting achievements.
 * </p>
 *
 * Additional query methods:
 * <ul>
 *     <li>{@link #findByCode(String)} – look up an achievement by its unique code.</li>
 * </ul>
 */
public interface AchievementRepository extends JpaRepository<Achievement, UUID> {

    /**
     * Finds an achievement by its unique code.
     *
     * @param code technical code of the achievement, e.g. "XP_100"
     * @return Optional containing the matching achievement if found, otherwise empty
     */
    Optional<Achievement> findByCode(String code);
}
