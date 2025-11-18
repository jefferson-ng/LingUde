package com.sep.sep_backend.user.controller;

import com.sep.sep_backend.user.dto.UserLearningDTO;
import com.sep.sep_backend.user.entity.UserLearning;
import com.sep.sep_backend.user.service.UserLearningService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

/**
 * REST Controller for managing user learning data and XP tracking.
 * Provides endpoints to retrieve and update user learning progress, including XP, streaks,
 * and language learning preferences.
 */
@RestController
@RequestMapping("/api/user/learning")
@CrossOrigin(origins = "http://localhost:4200")
public class UserLearningController {

    private final UserLearningService userLearningService;

    public UserLearningController(UserLearningService userLearningService) {
        this.userLearningService = userLearningService;
    }

    /**
     * Retrieves the learning data for a specific user by their user ID.
     * This includes XP, streak count, learning language, current level, and target level.
     *
     * @return ResponseEntity containing UserLearningDTO if found, or 404 NOT FOUND if user learning data doesn't exist
     */
    @GetMapping("/myLearning")
    public ResponseEntity<UserLearningDTO> getUserLearning() {
        try {
            // TEMPORARY: Use hardcoded test user ID when not authenticated
            UUID userId;
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UUID) {
                userId = (UUID) principal;
            } else {
                // Use test user ID when not authenticated
                userId = UUID.fromString("e53351ef-fed0-487e-a93b-604a94e89b0d");
            }
            Optional<UserLearning> learningOptional = userLearningService.findLearningByUserId(userId);
            
            if (learningOptional.isPresent()) {
                UserLearning learning = learningOptional.get();
                UserLearningDTO dto = new UserLearningDTO(
                    learning.getUser().getId().toString(),
                    learning.getLearningLanguage(),
                    learning.getCurrentLevel(),
                    learning.getTargetLevel(),
                    learning.getXp(),
                    learning.getStreakCount(),
                    learning.getLastActivityDate()
                );
                return ResponseEntity.ok(dto);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Adds XP to a user's learning progress.
     *
     * @param xpAmount the amount of XP to add (passed as request parameter)
     * @return ResponseEntity containing updated UserLearningDTO if successful, or 404 NOT FOUND if user not found
     */
    @PostMapping("/addXp")
    public ResponseEntity<UserLearningDTO> addXp(
            @RequestParam Integer xpAmount) {
        try {
            // TEMPORARY: Use hardcoded test user ID when not authenticated
            UUID userId;
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UUID) {
                userId = (UUID) principal;
            } else {
                // Use test user ID when not authenticated
                userId = UUID.fromString("e53351ef-fed0-487e-a93b-604a94e89b0d");
            }
            Optional<UserLearning> learningOptional = userLearningService.addXp(userId, xpAmount);
            
            if (learningOptional.isPresent()) {
                UserLearning learning = learningOptional.get();
                UserLearningDTO dto = new UserLearningDTO(
                    learning.getUser().getId().toString(),
                    learning.getLearningLanguage(),
                    learning.getCurrentLevel(),
                    learning.getTargetLevel(),
                    learning.getXp(),
                    learning.getStreakCount(),
                    learning.getLastActivityDate()
                );
                return ResponseEntity.ok(dto);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Updates the user's learning language and CEFR levels
     * (currentLevel and targetLevel) in one request.
     * <p>
     * This endpoint is used by the language level selection flow.
     *
     * @param dto    DTO with learningLanguage, currentLevel, targetLevel
     * @return 200 with updated DTO, 404 if no learning data exists,
     *         or 400 if the userId is not a valid UUID
     */
    @PutMapping("/myLearning")
    public ResponseEntity<UserLearningDTO> updateUserLevels(
            @RequestBody UserLearningDTO dto) {
        // TEMPORARY: Use hardcoded test user ID when not authenticated
        UUID userId;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UUID) {
            userId = (UUID) principal;
        } else {
            // Use test user ID when not authenticated
            userId = UUID.fromString("e53351ef-fed0-487e-a93b-604a94e89b0d");
        }
        try {

            Optional<UserLearningDTO> updated =
                    userLearningService.updateLearningConfig(userId, dto);

            return updated
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}
