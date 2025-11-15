package com.sep.sep_backend.exercise.repository;

import com.sep.sep_backend.exercise.entity.ExerciseType;
import com.sep.sep_backend.exercise.entity.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing persistence and retrieval of
 * {@link UserProgress} entries.
 *
 * <p>
 * This repository provides high-level data access for tracking a user's
 * completion state across exercises. It supports:
 * </p>
 *
 * <ul>
 *     <li>Fetching an existing progress entry for a specific
 *         combination of user, exercise ID, and exercise type</li>
 *     <li>Counting how many exercises a user has already completed</li>
 *     <li>All default CRUD operations inherited from {@link JpaRepository}</li>
 * </ul>
 *
 * <p>
 * The service layer relies on this repository to update progress after
 * exercise submissions and to compute progress statistics for sessions
 * or lessons.
 * </p>
 */

public interface UserProgressRepository extends JpaRepository<UserProgress, UUID> {
    /**
     * Retrieves a progress entry for a specific user, exercise, and exercise type.
     *
     * <p>
     * This method is used by the submission workflow to determine whether
     * the user has already interacted with this exercise and whether the
     * existing entry should be updated.
     * </p>
     *
     * @param userId        The unique identifier of the user.
     * @param exerciseId    The unique identifier of the exercise.
     * @param exerciseType  The type of the exercise (e.g., MCQ, FILL_BLANK).
     * @return An {@link Optional} containing the matching {@link UserProgress}
     *         entity if it exists, or an empty Optional if not found.
     */

    Optional<UserProgress> findByUserIdAndExerciseIdAndExerciseType(
            UUID userId, UUID exerciseId, ExerciseType exerciseType
    );

    /**
     * Counts how many exercises have been completed by the user.
     *
     * <p>
     * This method queries the database for {@link UserProgress} records
     * belonging to the specified user where the {@code is_completed} flag
     * is set to {@code true}. It is primarily used for calculating the
     * user's overall or session-based progress.
     * </p>
     *
     * <p>
     * For example, if a user has completed 7 out of 12 exercises, this
     * method will return {@code 7}.
     * </p>
     *
     * @param userId The unique identifier of the user.
     * @return The total number of exercises marked as completed for this user.
     */

    long countByUserIdAndIsCompletedTrue(UUID userId);
}
