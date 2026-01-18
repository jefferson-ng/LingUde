package com.sep.sep_backend.exercise.service;

import com.sep.sep_backend.exercise.dto.*;
import com.sep.sep_backend.exercise.entity.*;
import com.sep.sep_backend.exercise.repository.ExerciseFillBlankRepository;
import com.sep.sep_backend.exercise.repository.ExerciseMcqRepository;
import com.sep.sep_backend.exercise.repository.UserProgressRepository;
import com.sep.sep_backend.user.entity.User;
import com.sep.sep_backend.user.service.UserLearningService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Central service for exercise handling.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Load MCQ and Fill-in-the-Blank exercises from the database</li>
 *   <li>Convert entities into API DTOs for list & detail views</li>
 *   <li>Check user submissions, calculate XP and feedback</li>
 *   <li>Update or create {@link UserProgress} entries when a user solves an exercise</li>
 * </ul>
 * This class is intentionally type-aware: it knows how to handle both
 * {@link ExerciseType#MCQ} and {@link ExerciseType#FILL_BLANK}. It is
 * authentication-agnostic in the sense that it only works with a userId
 * ({@link java.util.UUID}) provided by upper layers (e.g. controllers
 * reading from Spring Security), and does not access the security context
 * directly.
 * </p>
 */
@Service
public class ExerciseService {

    private final ExerciseMcqRepository mcqRepo;
    private final ExerciseFillBlankRepository fillRepo;
    private final UserProgressRepository progressRepo;
    private final UserLearningService userLearningService;

    public ExerciseService(ExerciseMcqRepository mcqRepo,
                           ExerciseFillBlankRepository fillRepo,
                           UserProgressRepository progressRepo,
                           UserLearningService userLearningService) {
        this.mcqRepo = mcqRepo;
        this.fillRepo = fillRepo;
        this.progressRepo = progressRepo;
        this.userLearningService = userLearningService;
    }

    // ---------- List & detail ----------

    /**
     * Loads all MCQ exercises from the database and maps them to summary DTOs.
     * The summary is designed for listing screens (cards).
     *
     * @return list of {@link ExerciseSummaryResponse} for MCQ exercises
     */
    public List<ExerciseSummaryResponse> listMcq() {
        List<ExerciseMcq> all = mcqRepo.findAll();
        List<ExerciseSummaryResponse> out = new ArrayList<>();
        for (ExerciseMcq e : all) {
            out.add(new ExerciseSummaryResponse(
                    e.getId(), ExerciseType.MCQ,
                    e.getContentType(),
                    (e.getTargetLanguage()  != null ? e.getTargetLanguage().name()  : null),
                    (e.getDifficultyLevel() != null ? e.getDifficultyLevel().name() : null),
                    e.getTopic(), e.getXpReward(),
                    e.getQuestionText()
            ));

        }
        return out;
    }
    /**
     * Loads all Fill-in-the-Blank exercises and maps them to summary DTOs.
     *
     * @return list of {@link ExerciseSummaryResponse} for Fill-in-the-Blank exercises
     */
    public List<ExerciseSummaryResponse> listFillBlank() {
        List<ExerciseFillBlank> all = fillRepo.findAll();
        List<ExerciseSummaryResponse> out = new ArrayList<>();
        for (ExerciseFillBlank e : all) {
            out.add(new ExerciseSummaryResponse(
                    e.getId(), ExerciseType.FILL_BLANK,
                    e.getContentType(),
                    (e.getTargetLanguage()  != null ? e.getTargetLanguage().name()  : null),
                    (e.getDifficultyLevel() != null ? e.getDifficultyLevel().name() : null),
                    e.getTopic(), e.getXpReward(),
                    e.getSentenceWithBlank()
            ));

        }
        return out;
    }

    /**
     * Returns the detailed view for a single MCQ exercise.
     * The method also shuffles the answer options so that the
     * correct answer is not always at the same position.
     *
     * @param id ID of the MCQ exercise
     * @return {@link ExerciseDetailResponse} containing all information for the MCQ
     * @throws NoSuchElementException if no exercise with the given ID exists
     */
    public ExerciseDetailResponse getMcq(UUID id) {
        ExerciseMcq e = mcqRepo.findById(id).orElseThrow(() -> new NoSuchElementException("MCQ not found"));
        ExerciseDetailResponse dto = new ExerciseDetailResponse();
        dto.setId(e.getId());
        dto.setType(ExerciseType.MCQ);
        dto.setContentType(e.getContentType());
        dto.setTargetLanguage(e.getTargetLanguage() != null ? e.getTargetLanguage().name() : null);
        dto.setDifficultyLevel(e.getDifficultyLevel() != null ? e.getDifficultyLevel().name() : null);
        dto.setTopic(e.getTopic());
        dto.setXpReward(e.getXpReward());
        dto.setQuestionText(e.getQuestionText());
        // collect and shuffle all answer options
        List<String> options = new ArrayList<>(Arrays.asList(
                e.getCorrectAnswer(), e.getWrongOption1(), e.getWrongOption2(), e.getWrongOption3()
        ));
        Collections.shuffle(options);
        dto.setOptions(options);
        return dto;
    }

    /**
     * Returns the detailed view for a single Fill-in-the-Blank exercise.
     *
     * @param id ID of the Fill-in-the-Blank exercise
     * @return {@link ExerciseDetailResponse} containing sentence and meta information
     * @throws NoSuchElementException if no exercise with the given ID exists
     */
    public ExerciseDetailResponse getFillBlank(UUID id) {
        ExerciseFillBlank e = fillRepo.findById(id).orElseThrow(() -> new NoSuchElementException("Fill-Blank not found"));
        ExerciseDetailResponse dto = new ExerciseDetailResponse();
        dto.setId(e.getId());
        dto.setType(ExerciseType.FILL_BLANK);
        dto.setContentType(e.getContentType());
        dto.setTargetLanguage(e.getTargetLanguage() != null ? e.getTargetLanguage().name() : null);
        dto.setDifficultyLevel(e.getDifficultyLevel() != null ? e.getDifficultyLevel().name() : null);
        dto.setTopic(e.getTopic());
        dto.setXpReward(e.getXpReward());
        dto.setSentenceWithBlank(e.getSentenceWithBlank());
        return dto;
    }

    // ---------- Submission & progress ----------

    /**
     * Processes a submission for a MCQ exercise.
     * <ul>
     *   <li>checks whether the selected answer matches the correct answer</li>
     *   <li>computes the XP to award</li>
     *   <li>updates {@link UserProgress} if a user is provided</li>
     * </ul>
     *
     <p>
     * Authentication is handled at the controller level. The controller is expected
     * to extract the authenticated user's ID (e.g. from Spring Security) and pass it
     * to this method. If {@code userId} is {@code null}, progress is not recorded,
     * but the correctness and XP calculation still work.
     * </p>
     *
     * @param id   ID of the MCQ exercise
     * @param req  payload containing the selected answer
     * @param userId Unique identifier of the authenticated user, or {@code null}
     *               if progress tracking should be skipped.
     * @return {@link SubmissionResultResponse} with correctness, XP and feedback
     */
    @Transactional
    public SubmissionResultResponse submitMcq(UUID id, McqSubmissionRequest req, UUID userId) {
        ExerciseMcq e = mcqRepo.findById(id).orElseThrow(() -> new NoSuchElementException("MCQ not found"));
        boolean correct = e.getCorrectAnswer().equals(req.getSelectedAnswer());
        int xp = correct ? e.getXpReward() : 0;
        updateUserProgress(userId, id, ExerciseType.MCQ, correct, xp);
        String feedback = correct ? "Great job!" : "Try again.";
        return new SubmissionResultResponse(correct, xp, e.getCorrectAnswer(), feedback);
        // NOTE: In production, you may omit correctAnswer from response to prevent leaks.
    }

    /**
     * Processes a submission for a Fill-in-the-Blank exercise.
     * The comparison is normalized (trimmed, lower-cased, single spaces)
     * so that small formatting differences do not cause a wrong result.
     *
     * <p>
     * Authentication is handled at the controller level. The controller is expected
     * to extract the authenticated user's ID (e.g. from Spring Security) and pass it
     * to this method. If {@code userId} is {@code null}, progress is not recorded,
     * but answer evaluation still works.
     * </p>
     *
     * @param id   ID of the Fill-in-the-Blank exercise
     * @param req  payload containing the user answer text
     * @param userId Unique identifier of the authenticated user, or {@code null}
     *               if progress tracking should be skipped.
     * @return {@link SubmissionResultResponse} with correctness, XP and feedback
     */
    @Transactional
    public SubmissionResultResponse submitFillBlank(UUID id, FillBlankSubmissionRequest req, UUID userId) {
        ExerciseFillBlank e = fillRepo.findById(id).orElseThrow(() -> new NoSuchElementException("Fill-Blank not found"));
        String userAns = normalize(req.getAnswerText());
        String sol = normalize(e.getCorrectAnswer());
        boolean correct = userAns.equals(sol);
        int xp = correct ? e.getXpReward() : 0;
        updateUserProgress(userId, id, ExerciseType.FILL_BLANK, correct, xp);
        String feedback = correct ? "Nice!" : "Remember the correct form.";
        return new SubmissionResultResponse(correct, xp, e.getCorrectAnswer(), feedback);
    }

    /**
     * Normalizes an answer for comparison:
     * <ul>
     *   <li>treats {@code null} as empty string</li>
     *   <li>trims leading/trailing whitespace</li>
     *   <li>converts to lower case</li>
     *   <li>collapses multiple spaces into a single space</li>
     * </ul>
     *
     * @param s raw user input or solution
     * @return normalized string never {@code null}
     */
    private String normalize(String s) {
        if (s == null) return "";
        return s.trim().toLowerCase().replaceAll("\\s+", " ");
    }

    /**
     * Updates the progress of a user for a specific exercise based on their submission result.
     *
     * <p>
     * This method is responsible for creating or updating a {@link UserProgress} entry
     * for the given user and exercise. If it is the first submission, a new progress
     * record is inserted. If a record already exists and was not yet completed, it can
     * be updated when the user finally submits a correct answer.
     * </p>
     *
     * <p>
     * If {@code userId} is {@code null}, the method returns immediately and no
     * progress is recorded. This allows the service to be called in contexts
     * where tracking is not required (although in the current application all
     * exercise endpoints are authenticated).
     * </p>
     * @param userId     The unique identifier of the user whose progress is being updated.
     * @param exerciseId The unique identifier of the exercise being attempted.
     * @param type The type of the exercise being attempted.
     * @param correct Indicates whether the user's submission was correct.
     * @param xp The amount of experience points (XP) earned from the correct submission.
     */
    private void updateUserProgress(UUID userId, UUID exerciseId, ExerciseType type, boolean correct, int xp) {
        if (userId == null) return;

        // Look up an existing progress entry for this user and exercise.
        UserProgress existing = progressRepo
                .findByUserIdAndExerciseIdAndExerciseType(userId, exerciseId, type)
                .orElse(null);

        boolean shouldUpdateStreak = false;

        if (existing == null) {
            // First submission for this user/exercise
            // We create a lightweight User reference with only the ID set, so JPA can
            // persist the foreign key without needing to load the full User entity.
            User userRef = new User();
            userRef.setId(userId);

            UserProgress up = new UserProgress(userRef, exerciseId, type);
            if (correct) {
                up.setIsCompleted(true);
                up.setCompletedAt(LocalDateTime.now());
                up.setXpEarned(xp);
                shouldUpdateStreak = true; // First successful completion today
            } else {
                // Track incorrect attempt
                up.incrementIncorrectAttempts();
            }
            progressRepo.save(up);
        } else {
            // Only update if it was not completed before and the new submission is correct
            if (!Boolean.TRUE.equals(existing.getIsCompleted()) && correct) {
                existing.setIsCompleted(true);
                existing.setCompletedAt(LocalDateTime.now());
                existing.setXpEarned(xp);
                shouldUpdateStreak = true; // First successful completion today
                progressRepo.save(existing);
            } else if (!Boolean.TRUE.equals(existing.getIsCompleted()) && !correct) {
                // Increment incorrect attempts if answer is wrong and not yet completed
                existing.incrementIncorrectAttempts();
                progressRepo.save(existing);
            }
        }

        // Add XP only when first successful exercise completion happens
        if (shouldUpdateStreak) {
            userLearningService.addXp(userId, xp);
        }
    }
    /**
     * Computes the progress of a user within a given lesson or exercise session.
     *
     * <p>
     * At the current stage of the application, there is no dedicated domain model
     * for sessions or lessons. Therefore, a "session" is interpreted as:
     * </p>
     *
     * <ul>
     *     <li><b>All available exercises</b> in the system (MCQ + Fill-in-the-Blank).</li>
     *     <li>This is a temporary definition that will be replaced once sessions,
     *         lessons or category-based classifications (e.g. vocabulary, synonym,
     *         grammar) are introduced.</li>
     * </ul>
     *
     * <p><b>Behaviour:</b></p>
     * <ul>
     *     <li>If {@code userId} is {@code null}, no user context is available and a
     *         progress of <code>(0 / 0)</code> is returned.</li>
     *     <li>Otherwise, the method:</li>
     *     <ul>
     *         <li>Counts how many exercises the user has completed using
     *             {@link com.sep.sep_backend.exercise.repository.UserProgressRepository#countByUserIdAndIsCompletedTrue(UUID)}.</li>
     *         <li>Counts how many exercises exist in total using
     *             {@link com.sep.sep_backend.exercise.repository.ExerciseMcqRepository#count()}
     *             and {@link com.sep.sep_backend.exercise.repository.ExerciseFillBlankRepository#count()}.</li>
     *         <li>Ensures the completed count never exceeds the total count.</li>
     *     </ul>
     * </ul>
     *
     * <p><b>Future extension:</b></p>
     * <p>
     * When the domain model introduces:
     * </p>
     * <ul>
     *     <li>Exercise classifications (VOCABULARY, SYNONYM, GRAMMAR), or</li>
     *     <li>Dedicated lesson/session entities grouping exercises,</li>
     * </ul>
     * <p>
     * this method can be easily updated to:
     * </p>
     * <ul>
     *     <li>Filter exercises belonging to the session/category.</li>
     *     <li>Count only the user's completed exercises within that subset.</li>
     * </ul>
     *
     * <p>
     * The method signature and the {@link SessionProgressResponse}
     * DTO are expected to remain stable — only the internal query logic will evolve.
     * This makes the design stable and future-proof.
     * </p>
     *
     * @param sessionId An identifier for the session or lesson.
     *                  Currently unused, but preserved for forward compatibility.
     * @param userId    The unique identifier of the authenticated user whose
     *                  progress should be computed. May be {@code null} in edge cases.
     * @return A {@link SessionProgressResponse} containing the number of completed
     *         exercises and the total number of exercises in the "session".
     */
    @Transactional(readOnly = true)
    public SessionProgressResponse getSessionProgress(UUID sessionId, UUID userId) {

        // If no authenticated userId is provided, progress cannot be tracked.
        // Return 0/0 as a safe fallback.
        if (userId == null) {
            return new SessionProgressResponse(0, 0);
        }

        // Count how many exercises this user has completed.
        // This checks the user_progress table for rows where:
        // user_id = userId AND is_completed = true
        long completed = progressRepo.countByUserIdAndIsCompletedTrue(userId);

        // Count ALL available exercises in the system.
        // This is MCQ count + Fill-in-the-Blank count.
        // (In the future this will be filtered by session/category.)
        long totalExercises = mcqRepo.count()    // number of MCQ exercises
                + fillRepo.count();              // number of Fill-in-the-Blank exercises

        // Convert total exercises to int for the DTO.
        int totalCount = (int) totalExercises;

        // Defensive: ensure completedCount does not exceed totalCount.
        // (Should never happen, but safe.)
        int completedCount = (int) Math.min(completed, totalExercises);

        // Return the final progress object containing:
        // - completed exercises
        // - total exercises
        return new SessionProgressResponse(completedCount, totalCount);
    }

    // ---------- Progress saving (read access) ----------

    /**
     * Returns all completed exercises for a given user.
     *
     * This method reads from the user_progress table and finds every exercise
     * where:
     *   - user_id = userId
     *   - is_completed = true
     *
     * It is used when the frontend needs to show:
     *   - which exercises the user finished
     *   - completed checkmarks (✔)
     *   - progress overview
     *
     * If userId is null (should not happen), an empty list is returned.
     *
     * @param userId UUID of the authenticated user. If {@code null}, an empty list is returned.
     * @return list of completed {@link UserProgress} entries for this user.
     */
    @Transactional(readOnly = true)
    public List<UserProgress> getCompletedExercisesForUser(UUID userId) {
        if (userId == null) {
            // No authenticated user → no progress to return
            return List.of();
        }
        return progressRepo.findAllByUserIdAndIsCompletedTrue(userId);
    }

    /**
     * Retrieves all exercises with incorrect attempts that are not yet completed.
     * These are exercises the user can retry.
     *
     * @param userId UUID of the authenticated user. If {@code null}, an empty list is returned.
     * @return list of {@link UserProgress} entries with incorrect attempts and not completed.
     */
    @Transactional(readOnly = true)
    public List<UserProgress> getIncorrectExercisesForUser(UUID userId) {
        if (userId == null) {
            return List.of();
        }
        return progressRepo.findAllByUserIdAndIncorrectAttemptsGreaterThanAndIsCompletedFalse(userId, 0);
    }

    /**
     * Checks whether the user has already completed a specific exercise.
     *
     * This is a quick YES/NO check used when the UI needs to know if an exercise
     * should show a completed marker (✔) without loading the full progress object.
     *
     * @return true  → user completed this exercise
     *         false → user has not completed it
     *
     * @param userId     UUID of the user (may be {@code null}).
     * @param exerciseId UUID of the exercise.
     * @param type       type of the exercise (MCQ, FILL_BLANK, ...).
     *
     */
    @Transactional(readOnly = true)
    public boolean hasUserCompletedExercise(UUID userId, UUID exerciseId, ExerciseType type) {
        if (userId == null) {
            return false;
        }
        return progressRepo.existsByUserIdAndExerciseIdAndExerciseTypeAndIsCompletedTrue(
                userId,
                exerciseId,
                type
        );
    }

}
