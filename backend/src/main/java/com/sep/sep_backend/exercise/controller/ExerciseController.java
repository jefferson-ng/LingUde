package com.sep.sep_backend.exercise.controller;

import com.sep.sep_backend.exercise.dto.*;
import com.sep.sep_backend.exercise.service.ExerciseService;
import com.sep.sep_backend.user.entity.User;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// You can add @RequiredArgsConstructor if you use Lombok
@Validated
@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    private final ExerciseService service;

    public ExerciseController(ExerciseService service) {
        this.service = service;
    }

    // ---------- List & detail ----------

    @GetMapping(value = "/mcq", produces = "application/json")
    public List<ExerciseSummaryResponse> listMcq() {
        return service.listMcq();
    }

    @GetMapping(value = "/fillblank", produces = "application/json")
    public List<ExerciseSummaryResponse> listFillBlank() {
        return service.listFillBlank();
    }

    @GetMapping(value = "/mcq/{id}", produces = "application/json")
    public ExerciseDetailResponse getMcq(@PathVariable UUID id) {
        return service.getMcq(id);
    }

    @GetMapping(value = "/fillblank/{id}", produces = "application/json")
    public ExerciseDetailResponse getFillBlank(@PathVariable UUID id) {
        return service.getFillBlank(id);
    }

    // ---------- Submit answers ----------
    // TODO: Replace dummy User with @AuthenticationPrincipal integration when auth is wired.

    @PostMapping(value = "/mcq/{id}/submit", consumes = "application/json", produces = "application/json")
    public SubmissionResultResponse submitMcq(@PathVariable UUID id,
                                              @Valid @RequestBody McqSubmissionRequest req) {
        User dummy = null; // plug your authenticated user here
        return service.submitMcq(id, req, dummy);
    }

    @PostMapping(value = "/fillblank/{id}/submit", consumes = "application/json", produces = "application/json")
    public SubmissionResultResponse submitFillBlank(@PathVariable UUID id,
                                                    @Valid @RequestBody FillBlankSubmissionRequest req) {
        User dummy = null; // plug your authenticated user here
        return service.submitFillBlank(id, req, dummy);
    }


}
