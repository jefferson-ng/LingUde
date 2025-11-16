package com.sep.sep_backend.exercise.service;

import com.sep.sep_backend.exercise.dto.*;
import com.sep.sep_backend.exercise.entity.*;
import com.sep.sep_backend.exercise.repository.ExerciseFillBlankRepository;
import com.sep.sep_backend.exercise.repository.ExerciseMcqRepository;
import com.sep.sep_backend.exercise.repository.UserProgressRepository;
import com.sep.sep_backend.user.entity.User;
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
 * {@link ExerciseType#MCQ} and {@link ExerciseType#FILL_BLANK}.
 */
@Service
public class ExerciseService {

    private final ExerciseMcqRepository mcqRepo;
    private final ExerciseFillBlankRepository fillRepo;
    private final UserProgressRepository progressRepo;

    public ExerciseService(ExerciseMcqRepository mcqRepo,
                           ExerciseFillBlankRepository fillRepo,
                           UserProgressRepository progressRepo) {
        this.mcqRepo = mcqRepo;
        this.fillRepo = fillRepo;
        this.progressRepo = progressRepo;
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
     * @param id   ID of the MCQ exercise
     * @param req  payload containing the selected answer
     * @param user currently logged-in user (may be {@code null} until auth is implemented)
     * @return {@link SubmissionResultResponse} with correctness, XP and feedback
     */
    @Transactional
    public SubmissionResultResponse submitMcq(UUID id, McqSubmissionRequest req, User user) {
        ExerciseMcq e = mcqRepo.findById(id).orElseThrow(() -> new NoSuchElementException("MCQ not found"));
        boolean correct = e.getCorrectAnswer().equals(req.getSelectedAnswer());
        int xp = correct ? e.getXpReward() : 0;
        updateUserProgress(user, id, ExerciseType.MCQ, correct, xp);
        String feedback = correct ? "Great job!" : "Try again.";
        return new SubmissionResultResponse(correct, xp, e.getCorrectAnswer(), feedback);
        // NOTE: In production, you may omit correctAnswer from response to prevent leaks.
    }

    /**
     * Processes a submission for a Fill-in-the-Blank exercise.
     * The comparison is normalized (trimmed, lower-cased, single spaces)
     * so that small formatting differences do not cause a wrong result.
     *
     * @param id   ID of the Fill-in-the-Blank exercise
     * @param req  payload containing the user answer text
     * @param user currently logged-in user (may be {@code null} until auth is implemented)
     * @return {@link SubmissionResultResponse} with correctness, XP and feedback
     */
    @Transactional
    public SubmissionResultResponse submitFillBlank(UUID id, FillBlankSubmissionRequest req, User user) {
        ExerciseFillBlank e = fillRepo.findById(id).orElseThrow(() -> new NoSuchElementException("Fill-Blank not found"));
        String userAns = normalize(req.getAnswerText());
        String sol = normalize(e.getCorrectAnswer());
        boolean correct = userAns.equals(sol);
        int xp = correct ? e.getXpReward() : 0;
        updateUserProgress(user, id, ExerciseType.FILL_BLANK, correct, xp);
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
     * If it's the first submission, creates a new progress entry. If the exercise is already
     * completed, it does not update the progress unless the current submission marks it as correct.
     *
     * @param user The user whose progress is being updated. If null, progress is not tracked.
     * @param exerciseId The unique identifier of the exercise being attempted.
     * @param type The type of the exercise being attempted.
     * @param correct Indicates whether the user's submission was correct.
     * @param xp The amount of experience points (XP) earned from the correct submission.
     */
    private void updateUserProgress(User user, UUID exerciseId, ExerciseType type, boolean correct, int xp) {
        // Until authentication is integrated we do not track progress for anonymous users.
        if (user == null) return; // plug in auth later
        UUID userId = user.getId();

        UserProgress existing = progressRepo
                .findByUserIdAndExerciseIdAndExerciseType(userId, exerciseId, type)
                .orElse(null);
        // First submission for this user/exercise
        if (existing == null) {
            UserProgress up = new UserProgress(user, exerciseId, type);
            if (correct) {
                up.setIsCompleted(true);
                up.setCompletedAt(LocalDateTime.now());
                up.setXpEarned(xp);
            }
            progressRepo.save(up);
        } else {
            // Only update if it was not completed before and the new submission is correct
            if (!Boolean.TRUE.equals(existing.getIsCompleted()) && correct) {
                existing.setIsCompleted(true);
                existing.setCompletedAt(LocalDateTime.now());
                existing.setXpEarned(xp);
                progressRepo.save(existing);
            }
        }
    }

    /**
     * Computes the progress of a user within a given lesson or exercise session.
     *
     * <p>
     * At the current stage of the application, there is no dedicated domain model
     * for sessions or lessons. Therefore, a \"session\" is interpreted as:
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
     *     <li>If {@code user} is {@code null}, no user context is available and a
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
     * <p><b>Future Extension:</b></p>
     * <p>
     * When the domain model introduces:
     * </p>
     *
     * <ul>
     *     <li>Exercise classifications (VOCABULARY, SYNONYM, GRAMMAR), or</li>
     *     <li>Dedicated lesson/session entities grouping exercises,</li>
     * </ul>
     *
     * <p>
     * this method can be easily updated to:
     * </p>
     *
     * <ul>
     *     <li>Filter exercises belonging to the session/category.</li>
     *     <li>Count only the user's completed exercises within that subset.</li>
     * </ul>
     *
     * <p>
     * The method signature and the {@link com.sep.sep_backend.exercise.dto.SessionProgressResponse}
     * DTO do <b>not</b> need to change — only the internal query logic will evolve.
     * This makes the design stable and future-proof.
     * </p>
     *
     * @param sessionId An identifier for the session or lesson.
     *                  Currently unused, but preserved for forward compatibility.
     * @param user      The currently authenticated user (or {@code null} when
     *                  authentication is not yet integrated).
     * @return A {@link SessionProgressResponse} containing the number of completed
     *         exercises and the total number of exercises in the \"session\".
     */
    @Transactional(readOnly = true)
    public SessionProgressResponse getSessionProgress(UUID sessionId, User user) {
        // Case 1: no authenticated user → cannot track progress
        if (user == null) {
            return new SessionProgressResponse(0, 0);
        }

        UUID userId = user.getId();

        // Count how many exercises this user has already completed
        long completed = progressRepo.countByUserIdAndIsCompletedTrue(userId);

        // Total number of exercises currently available in the system
        long totalExercises = mcqRepo.count() + fillRepo.count();

        int totalCount = (int) totalExercises;
        int completedCount = (int) Math.min(completed, totalExercises);

        return new SessionProgressResponse(completedCount, totalCount);
    }
}
