package com.sep.sep_backend.exercise.controller;

import com.sep.sep_backend.exercise.dto.*;
import com.sep.sep_backend.exercise.service.ExerciseService;
import com.sep.sep_backend.user.entity.User;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
 * </ul>
 * Authentication will be integrated later. For now, submissions use a dummy placeholder user.
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
    // TODO: Replace dummy User with @AuthenticationPrincipal integration when auth is wired.

    /**
     * Accepts a submission for a MCQ exercise.
     * Delegates validation, XP calculation and progress tracking to {@link ExerciseService}.
     *
     * @param id  UUID of the MCQ exercise being submitted
     * @param req request body containing the selected answer
     * @return {@link SubmissionResultResponse} including correctness, XP and feedback
     */
    @PostMapping(value = "/mcq/{id}/submit", consumes = "application/json", produces = "application/json")
    public SubmissionResultResponse submitMcq(@PathVariable UUID id,
                                              @Valid @RequestBody McqSubmissionRequest req) {
        User dummy = null; // will be replaced by authenticated user once security is added
        return service.submitMcq(id, req, dummy);
    }

    /**
     * Accepts a submission for a Fill-in-the-Blank exercise.
     * Delegates answer comparison and XP reward logic to {@link ExerciseService}.
     *
     * @param id  UUID of the Fill-in-the-Blank exercise
     * @param req request body containing the user's typed answer
     * @return {@link SubmissionResultResponse} describing the evaluation outcome
     */
    @PostMapping(value = "/fillblank/{id}/submit", consumes = "application/json", produces = "application/json")
    public SubmissionResultResponse submitFillBlank(@PathVariable UUID id,
                                                    @Valid @RequestBody FillBlankSubmissionRequest req) {
        User dummy = null; // will be replaced by authenticated user once security is added
        return service.submitFillBlank(id, req, dummy);
    }


}
