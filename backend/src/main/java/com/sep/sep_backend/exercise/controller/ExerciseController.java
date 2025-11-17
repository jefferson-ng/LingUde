package com.sep.sep_backend.exercise.controller;

import com.sep.sep_backend.exercise.dto.*;
import com.sep.sep_backend.exercise.service.ExerciseService;
import com.sep.sep_backend.user.entity.User;
import jakarta.validation.Valid;
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
    // TODO: Replace dummy User with @AuthenticationPrincipal integration when auth is wired.

    /**
     * Accepts a submission for a MCQ exercise.
     *
     * <p>
     * The method:
     * </p>
     * <ul>
     *     <li>Extracts the authenticated user's ID from the {@link Authentication} object.</li>
     *     <li>Delegates validation, XP calculation and progress tracking to {@link ExerciseService}.</li>
     *     <li>Returns a {@link SubmissionResultResponse} containing correctness, XP and feedback.</li>
     * </ul>
     *
     * @param id  UUID of the MCQ exercise being submitted
     * @param req request body containing the selected answer
     * @param authentication Spring Security authentication containing the authenticated user's ID.
     * @return {@link SubmissionResultResponse} including correctness, XP and feedback
     */
    @PostMapping(value = "/mcq/{id}/submit", consumes = "application/json", produces = "application/json")
    public SubmissionResultResponse submitMcq(@PathVariable UUID id,
                                              @Valid @RequestBody McqSubmissionRequest req,Authentication authentication) {
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
     *     <li>Extracts the authenticated user's ID from the {@link Authentication} object.</li>
     *     <li>Delegates answer comparison and XP reward logic to {@link ExerciseService}.</li>
     *     <li>Returns a {@link SubmissionResultResponse} describing the evaluation outcome.</li>
     * </ul>
     *
     * @param id  UUID of the Fill-in-the-Blank exercise
     * @param req request body containing the user's typed answer
     * @param authentication Spring Security authentication containing the authenticated user's ID.
     * @return {@link SubmissionResultResponse} describing the evaluation outcome
     */
    @PostMapping(value = "/fillblank/{id}/submit", consumes = "application/json", produces = "application/json")
    public SubmissionResultResponse submitFillBlank(@PathVariable UUID id,
                                                    @Valid @RequestBody FillBlankSubmissionRequest req, Authentication authentication) {
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
     *         underlying service currently interprets a "session" as the
     *         set of all available exercises (MCQ + Fill-in-the-Blank).</li>
     *     <li>If authentication is missing or the principal is not a {@link UUID},
     *         the method returns a progress of {@code 0 / 0} as a safe fallback.</li>
     * </ul>
     *
     * <p><b>Future behaviour:</b></p>
     * <ul>
     *     <li>Once exercises are grouped into lessons or classified by
     *         category (e.g. vocabulary, synonym, grammar), the service
     *         method {@link com.sep.sep_backend.exercise.service.ExerciseService#getSessionProgress(UUID, UUID)}
     *         can be adapted to filter exercises based on {@code sessionId}.</li>
     *     <li>The signature of this controller method and the
     *         {@link SessionProgressResponse} DTO are expected to remain
     *         stable, so consumers of this API do not need to change.</li>
     * </ul>
     *
     * @param sessionId      The identifier of the lesson or session whose progress
     *                       should be retrieved.
     * @param authentication The Spring Security authentication object containing
     *                       the authenticated user's ID as principal.
     * @return A {@link SessionProgressResponse} describing the user's progress
     *         in the requested session.
     */
    @GetMapping(value = "/session/{sessionId}", produces = "application/json")
    public SessionProgressResponse getSessionProgress(@PathVariable UUID sessionId,
                                                      Authentication authentication) {

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

}
