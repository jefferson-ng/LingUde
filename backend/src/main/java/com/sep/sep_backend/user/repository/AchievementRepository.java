package com.sep.sep_backend.user.repository;

import com.sep.sep_backend.user.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository for managing {@link Achievement} entities.
 * <p>
 * This provides basic CRUD operations for the achievements catalog:
 * creating, reading, updating and deleting achievements.
 * </p>
 *
 * Typically used to:
 * <ul>
 *     <li>Load all available achievements for seeding or admin views.</li>
 *     <li>Look up a specific achievement by its ID or code (custom method later).</li>
 * </ul>
 */
public interface AchievementRepository extends JpaRepository<Achievement, UUID> {
    // later we can add: Optional<Achievement> findByCode(String code);
}
