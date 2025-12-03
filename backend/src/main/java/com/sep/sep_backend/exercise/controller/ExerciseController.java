package com.sep.sep_backend.exercise.controller;

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
                                              @Valid @RequestBody McqSubmissionRequest req) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Extract the authenticated userId (or null if not available).
        UUID userId = extractUserId(authentication);

        // Delegate the evaluation and progress update to the service layer.
        return service.submitMcq(id, req, userId);
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
                                                    @Valid @RequestBody FillBlankSubmissionRequest req) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Extract the authenticated userId (or null if not available).
        UUID userId = extractUserId(authentication);

        // Delegate the evaluation and progress update to the service layer.
        return service.submitFillBlank(id, req, userId);
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
     * Returns all exercises that the authenticated user has completed.
     *
     * Used by the frontend to display:
     *  - a list of completed exercises
     *  - progress pages / statistics
     *  - completed ✔ markers in the UI
     *
     * @param auth Authentication object containing the user's UUID.
     * @return list of UserProgress entries where isCompleted = true.
     */
    @GetMapping("/completed")
    public List<UserProgress> getCompletedExercisesForUser(Authentication auth) {

        // Extract the authenticated user's UUID from the security context.
        UUID userId = UUID.fromString(auth.getName());

        // Call the service layer to retrieve ALL completed exercises
        // (UserProgress rows where isCompleted = true).
        return service.getCompletedExercisesForUser(userId);
    }

    /**
     * Checks whether the authenticated user has completed a specific exercise.
     *
     * This is a quick YES/NO endpoint used by the frontend to decide
     * whether an exercise should show a completed checkmark (✔).
     *
     * @param exerciseId The UUID of the exercise to check.
     * @param type       The type of the exercise (MCQ, FILL_BLANK, ...).
     * @param auth       Authentication object containing the user's UUID.
     * @return true if the user completed the exercise, false otherwise.
     */
    @GetMapping("/{exerciseId}/completed")
    public boolean hasUserCompletedExercise(
            @PathVariable UUID exerciseId,   // read exerciseId from the URL path
            @RequestParam ExerciseType type, // read exercise type from query parameter
            Authentication auth              // authenticated user (from JWT)
    ) {
        // Extract the authenticated user ID from the JWT token
        UUID userId = UUID.fromString(auth.getName());

        // Ask the service layer:
        // "Does a completed UserProgress entry exist for this user + exercise + type?"
        return service.hasUserCompletedExercise(userId, exerciseId, type);
    }



}
