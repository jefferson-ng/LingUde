package com.sep.sep_backend.exercise.repository;

import com.sep.sep_backend.exercise.entity.ExerciseType;
import com.sep.sep_backend.exercise.entity.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing persistence and lookup of
 * {@link UserProgress} entities.
 *
 * <p>
 * A {@link UserProgress} entry represents the relationship between a user
 * and a specific exercise, including whether the exercise has been
 * completed and how much XP was earned. This repository provides
 * convenience methods that the service layer uses to:
 * </p>
 *
 * <ul>
 *     <li>Check if a progress record already exists for a given
 *         user and exercise.</li>
 *     <li>Count how many exercises a user has already completed.</li>
 *     <li>Perform standard CRUD operations via {@link JpaRepository}.</li>
 * </ul>
 *
 * <p>
 * The interface is intentionally kept small and focused. As the domain
 * evolves (e.g. when exercises are grouped by lesson or classified into
 * categories such as vocabulary, synonym, grammar), additional query
 * methods can be introduced here without breaking existing callers.
 * </p>
 */
public interface UserProgressRepository extends JpaRepository<UserProgress, UUID> {

    /**
     * Finds a single progress entry for the given combination of
     * user, exercise ID and exercise type.
     *
     * <p>
     * This method is typically used during the submission workflow to
     * determine whether a {@link UserProgress} row already exists.
     * If an entry is found, the service can decide whether it needs
     * to be updated (e.g. marking an exercise as completed) or left
     * unchanged.
     * </p>
     *
     * @param userId       The unique identifier of the user.
     * @param exerciseId   The unique identifier of the exercise.
     * @param exerciseType The type of the exercise (e.g. MCQ, FILL_BLANK).
     * @return An {@link Optional} containing the matching
     *         {@link UserProgress} if found; otherwise, an empty Optional.
     */
    Optional<UserProgress> findByUserIdAndExerciseIdAndExerciseType(
            UUID userId,
            UUID exerciseId,
            ExerciseType exerciseType
    );

    /**
     * Counts how many exercises have been completed by the given user.
     *
     * <p>
     * This method queries all {@link UserProgress} entries for the
     * specified user where the {@code isCompleted} flag is set to
     * {@code true}. The result can be used to build progress indicators,
     * dashboards, or statistics about the user's learning activity.
     * </p>
     *
     * <p>
     * At the moment, this method returns a global count across all
     * exercises. In the future, as soon as exercises are grouped
     * (for example by lesson or by category such as vocabulary,
     * synonym, grammar), more specific counting methods can be added
     * that include additional filter criteria without changing this
     * existing signature.
     * </p>
     *
     * @param userId The unique identifier of the user.
     * @return The number of exercises marked as completed for this user.
     */
    long countByUserIdAndIsCompletedTrue(UUID userId);
}
