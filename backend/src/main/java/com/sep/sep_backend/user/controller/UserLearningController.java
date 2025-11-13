package com.sep.sep_backend.user.controller;

import com.sep.sep_backend.user.dto.UserLearningDTO;
import com.sep.sep_backend.user.entity.UserLearning;
import com.sep.sep_backend.user.service.UserLearningService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * @param userId the UUID of the user whose learning data is being requested
     * @return ResponseEntity containing UserLearningDTO if found, or 404 NOT FOUND if user learning data doesn't exist
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserLearningDTO> getUserLearning(@PathVariable String userId) {
        try {
            UUID userUuid = UUID.fromString(userId);
            Optional<UserLearning> learningOptional = userLearningService.findLearningByUserId(userUuid);
            
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
     * This endpoint is primarily for testing purposes and simulating XP gains.
     *
     * @param userId the UUID of the user to add XP to
     * @param xpAmount the amount of XP to add (passed as request parameter)
     * @return ResponseEntity containing updated UserLearningDTO if successful, or 404 NOT FOUND if user not found
     */
    @PostMapping("/{userId}/xp")
    public ResponseEntity<UserLearningDTO> addXp(
            @PathVariable String userId,
            @RequestParam Integer xpAmount) {
        try {
            UUID userUuid = UUID.fromString(userId);
            Optional<UserLearning> learningOptional = userLearningService.addXp(userUuid, xpAmount);
            
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
}
