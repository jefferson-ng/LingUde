package com.sep.sep_backend.exercise.repository;

import com.sep.sep_backend.exercise.entity.ExerciseType;
import com.sep.sep_backend.exercise.entity.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
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

    /**
     * Retrieves all completed exercises (UserProgress entries where isCompleted = true)
     * for a specific user.
     *
     * This is used by the frontend to display a list of completed exercises and
     * to place completion markers (✔) in the exercise selection screen.
     *
     * @param userId The unique identifier of the user.
     * @return A list of all completed UserProgress entries for the user.
     */
    List<UserProgress> findAllByUserIdAndIsCompletedTrue(UUID userId);

    /**
     * Checks whether a specific exercise has already been completed by the user.
     * ------------------------
     * Sometimes the frontend only needs a quick YES/NO answer to the question:
     *     "Has the user completed this exercise?"
     *
     * Instead of loading the entire UserProgress object (with XP, timestamps, etc.),
     * this method performs a very fast database check using SQL EXISTS.
     *
     * It only returns:
     *     true  → user has completed this exercise
     *     false → user has NOT completed this exercise
     *
     * Useful :
     * ---------------------
     * - When the exercise list page shows checkmarks (✔) for completed exercises
     * - When validating whether XP should be awarded or not
     * - When determining if a user can repeat an exercise for rewards
     * - When building dashboards or progress indicators
     *
     * Why is it fast?
     * ----------------
     * Spring Data JPA converts this into a highly optimized query:
     *
     * This is much faster than fetching the entire progress row.
     *
     * @param userId       The UUID of the user.
     * @param exerciseId   The UUID of the exercise.
     * @param exerciseType The type of the exercise (MCQ, FILL_BLANK, ...).
     * @return true if the exercise is already completed by the user; false otherwise.
     */
    boolean existsByUserIdAndExerciseIdAndExerciseTypeAndIsCompletedTrue(
            UUID userId,
            UUID exerciseId,
            ExerciseType exerciseType
    );

    /**
     * Retrieves all exercises that have at least one incorrect attempt and are not yet completed.
     * These are exercises the user answered incorrectly and can retry.
     *
     * @param userId The unique identifier of the user.
     * @return A list of UserProgress entries with incorrect attempts and not completed.
     */
    List<UserProgress> findAllByUserIdAndIncorrectAttemptsGreaterThanAndIsCompletedFalse(
            UUID userId,
            Integer minIncorrectAttempts
    );

}
