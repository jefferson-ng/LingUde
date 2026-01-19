package com.sep.sep_backend.exercise.controller;

import com.sep.sep_backend.exercise.dto.CompletedExerciseResponse;
import com.sep.sep_backend.exercise.dto.CompletionStatusResponse;




import com.sep.sep_backend.exercise.dto.*;
import com.sep.sep_backend.exercise.entity.ExerciseType;
import com.sep.sep_backend.exercise.entity.UserProgress;
import com.sep.sep_backend.exercise.service.ExerciseService;
import com.sep.sep_backend.user.entity.User;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * REST controller exposing API endpoints for exercise functionality.
 * <p>
 * Responsibilities:
 * <ul>
 *     <li>Provide listing APIs for MCQ and Fill-in-the-Blank exercises</li>
 *     <li>Provide detail endpoints for individual exercises</li>
 *     <li>Accept user submissions and delegate evaluation to the service layer</li>
 *     <li>Expose progress information for the authenticated user.</li>
 * </ul>
 *
 * <p>
 * Authentication is handled by Spring Security with a JWT filter
 * ({@code JwtAuthenticationFilter}). The filter stores the authenticated
 * user's {@link UUID} as the principal inside Spring Security's
 * {@link Authentication} object. This controller extracts that userId
 * and passes it to the {@link ExerciseService} for progress tracking.
 * </p>
 */
@Validated
@RestController
@RequestMapping("/api/exercises")
@CrossOrigin(origins = "http://localhost:4200")
public class ExerciseController {

    private final ExerciseService service;

    /**
     * Creates the controller and injects the exercise service.
     *
     * @param service business logic layer for exercises
     */
    public ExerciseController(ExerciseService service) {
        this.service = service;
    }

    // ---------- List & detail ----------

    /**
     * Returns a list of all available MCQ exercises.
     * This is typically used for rendering overview cards/lists in the frontend.
     *
     * @return list of {@link ExerciseSummaryResponse} objects for MCQ exercises
     */
    @GetMapping(value = "/mcq", produces = "application/json")
    public List<ExerciseSummaryResponse> listMcq() {
        return service.listMcq();
    }

    /**
     * Returns a list of all available Fill-in-the-Blank exercises.
     *
     * @return list of {@link ExerciseSummaryResponse} objects for fill-blank exercises
     */
    @GetMapping(value = "/fillblank", produces = "application/json")
    public List<ExerciseSummaryResponse> listFillBlank() {
        return service.listFillBlank();
    }

    /**
     * Returns the full details of a specific MCQ exercise, including
     * shuffled answer options and metadata such as XP reward and language.
     *
     * @param id unique identifier of the exercise
     * @return {@link ExerciseDetailResponse} with full MCQ details
     */
    @GetMapping(value = "/mcq/{id}", produces = "application/json")
    public ExerciseDetailResponse getMcq(@PathVariable UUID id) {
        return service.getMcq(id);
    }

    /**
     * Returns the full details for a specific Fill-in-the-Blank exercise.
     *
     * @param id UUID of the exercise
     * @return {@link ExerciseDetailResponse} containing metadata and sentence template
     */
    @GetMapping(value = "/fillblank/{id}", produces = "application/json")
    public ExerciseDetailResponse getFillBlank(@PathVariable UUID id) {
        return service.getFillBlank(id);
    }

    // ---------- Submit answers ----------
    /**
     * Accepts a submission for a MCQ (multiple-choice) exercise.
     *
     * <p>
     * The method:
     * </p>
     * <ul>
     *     <li>Retrieves the authenticated user's ID internally from
     *         Spring Security's {@link org.springframework.security.core.context.SecurityContextHolder}.</li>
     *     <li>Delegates answer validation, XP calculation, and progress tracking
     *         to the {@link ExerciseService}.</li>
     *     <li>Returns a {@link SubmissionResultResponse} containing correctness,
     *         XP earned, and feedback.</li>
     * </ul>
     *
     * @param id  UUID of the MCQ exercise being submitted
     * @param req request body containing the selected answer
     * @return {@link SubmissionResultResponse} including correctness, XP and feedback
     */

    @PostMapping(value = "/mcq/{id}/submit", consumes = "application/json", produces = "application/json")
    public SubmissionResultResponse submitMcq(@PathVariable UUID id,
                                              @Valid @RequestBody McqSubmissionRequest req,
                                              @RequestParam(defaultValue = "false") boolean isPracticeMode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Extract the authenticated userId (or null if not available).
        UUID userId = extractUserId(authentication);

        // Delegate the evaluation and progress update to the service layer.
        return service.submitMcq(id, req, userId, isPracticeMode);
    }

    /**
     * Accepts a submission for a Fill-in-the-Blank exercise.
     *
     * <p>
     * The method:
     * </p>
     * <ul>
     *     <li>Retrieves the authenticated user's ID internally from
     *         Spring Security's {@link org.springframework.security.core.context.SecurityContextHolder}.</li>
     *     <li>Delegates answer validation, XP reward logic, and progress tracking
     *         to the {@link ExerciseService}.</li>
     *     <li>Returns a {@link SubmissionResultResponse} describing the evaluation outcome.</li>
     * </ul>
     *
     * @param id  UUID of the Fill-in-the-Blank exercise
     * @param req request body containing the user's typed answer
     * @return {@link SubmissionResultResponse} describing the evaluation outcome
     */

    @PostMapping(value = "/fillblank/{id}/submit", consumes = "application/json", produces = "application/json")
    public SubmissionResultResponse submitFillBlank(@PathVariable UUID id,
                                                    @Valid @RequestBody FillBlankSubmissionRequest req,
                                                    @RequestParam(defaultValue = "false") boolean isPracticeMode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Extract the authenticated userId (or null if not available).
        UUID userId = extractUserId(authentication);

        // Delegate the evaluation and progress update to the service layer.
        return service.submitFillBlank(id, req, userId, isPracticeMode);
    }

    // ---------- Session / progress ----------

    /**
     * Returns the progress of the authenticated user within a given lesson or session.
     *
     * <p>
     * The endpoint exposes the number of exercises the user has completed and
     * the total number of exercises that belong to the session. The result is
     * returned as a {@link SessionProgressResponse} and typically used by the
     * frontend to render a numerical or graphical progress indicator.
     * </p>
     *
     * <p><b>Current behaviour:</b></p>
     * <ul>
     *     <li>Because there is no explicit session/lesson model yet, the
     *         underlying service currently interprets a "session" as the set
     *         of all available exercises (MCQ + Fill-in-the-Blank).</li>
     *     <li>The authenticated user's ID is extracted internally from
     *         Spring Security's {@link org.springframework.security.core.context.SecurityContextHolder}.
     *         If no authentication is available or the principal is not a {@link UUID},
     *         the method returns a progress of {@code 0 / 0} as a safe fallback.</li>
     * </ul>
     *
     * <p><b>Future behaviour:</b></p>
     * <ul>
     *     <li>Once exercises are grouped into lessons or classified by category
     *         (e.g., vocabulary, synonym, grammar), the service method
     *         {@link com.sep.sep_backend.exercise.service.ExerciseService#getSessionProgress(UUID, UUID)}
     *         can be adapted to filter exercises based on {@code sessionId}.</li>
     *     <li>The signature of this controller method and the
     *         {@link SessionProgressResponse} DTO are expected to remain stable,
     *         so consumers of this API do not need to change.</li>
     * </ul>
     *
     * @param sessionId The identifier of the lesson or session whose progress
     *                  should be retrieved.
     * @return A {@link SessionProgressResponse} describing the user's progress
     *         in the requested session.
     */

    @GetMapping(value = "/session/{sessionId}", produces = "application/json")
    public SessionProgressResponse getSessionProgress(@PathVariable UUID sessionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Extract the authenticated userId (or null if not available).
        UUID userId = extractUserId(authentication);

        // Delegate the progress calculation to the service layer.
        return service.getSessionProgress(sessionId, userId);
    }

    // ---------- Helper methods ----------

    /**
     * Extracts the authenticated user's ID from the Spring Security
     * {@link Authentication} object.
     *
     * <p>
     * The {@link com.sep.sep_backend.auth.filter.JwtAuthenticationFilter} stores the
     * userId as the principal, so this method checks if the principal is a
     * {@link UUID} and returns it. If the authentication is missing or the
     * principal has an unexpected type, {@code null} is returned.
     * </p>
     *
     * @param authentication The current authentication object provided by Spring Security.
     * @return The authenticated user's {@link UUID}, or {@code null} if not available.
     */
    private UUID extractUserId(Authentication authentication) {
        // If there is no authentication at all, we cannot determine a userId.
        if (authentication == null) {
            return null;
        }

        // The JwtAuthenticationFilter sets the principal to a UUID.
        Object principal = authentication.getPrincipal();

        // Only return the principal if it is actually a UUID.
        if (principal instanceof UUID) {
            return (UUID) principal;
        }

        // Fallback: unexpected principal type → treat as unauthenticated.
        return null;
    }

    /**
     * Returns all completed exercises for the authenticated user.
     *
     * Flow:
     *  1) Extract the userId from Authentication (auth.getName()).
     *  2) Fetch all completed progress entries via the service.
     *  3) Convert them into CompletedExerciseResponse DTOs.
     *
     * Used for:
     *  - Progress page
     *  - Marking completed exercises with ✔
     *  - Achievement/badge features
     */
    @GetMapping("/completed")
    public List<CompletedExerciseResponse> getCompletedExercisesForUser() {

        // Get auth object from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Extract userId from token (may return null in tests)
        UUID userId = extractUserId(authentication);

        List<UserProgress> progressList = service.getCompletedExercisesForUser(userId);

        List<CompletedExerciseResponse> response = new ArrayList<>();
        for (UserProgress up : progressList) {
            response.add(new CompletedExerciseResponse(
                    up.getExerciseId(),
                    up.getExerciseType(),
                    up.getXpEarned(),
                    up.getCompletedAt()
            ));
        }

        return response;
    }

    /**
     * Returns all exercises with incorrect attempts that are not yet completed.
     * These are exercises the user can retry.
     *
     * @return list of {@link CompletedExerciseResponse} representing incorrect exercises
     */
    @GetMapping("/incorrect")
    public List<CompletedExerciseResponse> getIncorrectExercisesForUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = extractUserId(authentication);

        List<UserProgress> progressList = service.getIncorrectExercisesForUser(userId);

        List<CompletedExerciseResponse> response = new ArrayList<>();
        for (UserProgress up : progressList) {
            response.add(new CompletedExerciseResponse(
                    up.getExerciseId(),
                    up.getExerciseType(),
                    0, // No XP earned yet
                    null // Not completed yet
            ));
        }

        return response;
    }


    /**
     * Checks whether a specific exercise is completed for the authenticated user.
     *
     * Flow:
     *  1) Extract userId from Authentication.
     *  2) Read exerciseId from the URL.
     *  3) Read exerciseType from URL query param (?type=MCQ).
     *  4) Ask service if user completed this exercise.
     *  5) Return a CompletionStatusResponse DTO.
     *
     * Used for:
     *  - Fast UI checks
     *  - Showing ✔ beside an exercise
     *  - Prevent awarding XP multiple times
     */
    @GetMapping("/{exerciseId}/completed")
    public CompletionStatusResponse hasUserCompletedExercise(
            @PathVariable UUID exerciseId,
            @RequestParam ExerciseType type
    ) {
        // Get authentication from SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UUID userId = extractUserId(authentication);

        boolean completed = service.hasUserCompletedExercise(userId, exerciseId, type);

        return new CompletionStatusResponse(exerciseId, type, completed);
    }


}
