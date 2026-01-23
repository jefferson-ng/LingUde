package com.sep.sep_backend.ai.controller;

import com.sep.sep_backend.ai.dto.PracticeSentenceResponse;
import com.sep.sep_backend.ai.dto.PronunciationAnalyzeResponse;
import com.sep.sep_backend.ai.service.AzureSpeechService;
import com.sep.sep_backend.ai.service.PracticeSentenceService;
import com.sep.sep_backend.user.entity.Language;
import com.sep.sep_backend.user.entity.LanguageLevel;
import com.sep.sep_backend.user.service.UserLearningService;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for speech pronunciation assessment.
 */
@RestController
@RequestMapping("/api/speech")
@CrossOrigin(origins = "http://localhost:4200")
public class SpeechController {

    private static final Logger log = LoggerFactory.getLogger(SpeechController.class);

    private final AzureSpeechService azureSpeechService;
    private final PracticeSentenceService practiceSentenceService;
    private final UserLearningService userLearningService;

    public SpeechController(
            AzureSpeechService azureSpeechService,
            PracticeSentenceService practiceSentenceService,
            UserLearningService userLearningService) {
        this.azureSpeechService = azureSpeechService;
        this.practiceSentenceService = practiceSentenceService;
        this.userLearningService = userLearningService;
    }

    /**
     * Get practice sentences from exercises based on user's learning language and level.
     */
    @GetMapping("/practice-sentences")
    public ResponseEntity<List<PracticeSentenceResponse>> getPracticeSentences(
            @RequestParam(defaultValue = "5") int count,
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());

        // Get user's learning data to determine language and level
        var userLearningOpt = userLearningService.findLearningByUserId(userId);
        if (userLearningOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var userLearning = userLearningOpt.get();
        Language language = userLearning.getLearningLanguage();
        LanguageLevel level = userLearning.getCurrentLevel();

        List<PracticeSentenceResponse> sentences = practiceSentenceService.getPracticeSentences(
                language,
                level,
                count
        );

        return ResponseEntity.ok(sentences);
    }

    @PostMapping(
        value = "/analyze",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<PronunciationAnalyzeResponse> analyzePronunciation(
        @RequestPart("audio") MultipartFile audio,
        @RequestPart("referenceText") @NotBlank String referenceText,
        @RequestPart(value = "language", required = false) String language,
        @RequestPart(value = "exerciseId", required = false) String exerciseIdStr,
        Authentication authentication) throws IOException {

        UUID userId = UUID.fromString(authentication.getName());
        if (audio.isEmpty()) {
            PronunciationAnalyzeResponse response = new PronunciationAnalyzeResponse();
            response.setSuccess(false);
            response.setError("Audio file is required.");
            return ResponseEntity.badRequest().body(response);
        }

        PronunciationAnalyzeResponse response = azureSpeechService.analyze(
            audio.getBytes(),
            referenceText,
            language
        );

        // Log response before sending to frontend
        log.info("Controller sending response - Success: {}, Accuracy: {}, Fluency: {}, Completeness: {}, Pronunciation: {}, Words count: {}",
            response.isSuccess(), response.getAccuracyScore(), response.getFluencyScore(),
            response.getCompletenessScore(), response.getPronunciationScore(),
            response.getWords() != null ? response.getWords().size() : 0);

        if (response.getWords() != null && !response.getWords().isEmpty()) {
            log.info("First word - Word: {}, AccuracyScore: {}, ErrorType: {}",
                response.getWords().get(0).getWord(),
                response.getWords().get(0).getAccuracyScore(),
                response.getWords().get(0).getErrorType());
        }

        // Award XP if this was an exercise-based practice and pronunciation was good
        if (response.isSuccess() && exerciseIdStr != null && !exerciseIdStr.isBlank()) {
            try {
                UUID exerciseId = UUID.fromString(exerciseIdStr);
                Integer baseXp = practiceSentenceService.getExerciseXpReward(exerciseId);

                if (baseXp != null) {
                    // Calculate XP based on pronunciation score
                    int xpToAward = calculatePronunciationXp(response.getPronunciationScore(), baseXp);

                    if (xpToAward > 0) {
                        userLearningService.addXp(userId, xpToAward);
                        response.setXpAwarded(xpToAward);
                    }
                }
            } catch (IllegalArgumentException e) {
                // Invalid UUID, skip XP award
            }
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Calculate XP based on pronunciation score.
     * 90-100: Full XP + bonus
     * 80-89: Full XP
     * 70-79: 75% XP
     * 60-69: 50% XP
     * <60: 25% XP
     */
    private int calculatePronunciationXp(Double score, int baseXp) {
        if (score == null) return 0;

        if (score >= 90) {
            return (int) (baseXp * 1.5); // Bonus for excellent pronunciation
        } else if (score >= 80) {
            return baseXp;
        } else if (score >= 70) {
            return (int) (baseXp * 0.75);
        } else if (score >= 60) {
            return (int) (baseXp * 0.5);
        } else {
            return (int) (baseXp * 0.25);
        }
    }
}
