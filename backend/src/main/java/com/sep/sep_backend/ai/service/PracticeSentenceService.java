package com.sep.sep_backend.ai.service;

import com.sep.sep_backend.ai.dto.PracticeSentenceResponse;
import com.sep.sep_backend.exercise.entity.ExerciseFillBlank;
import com.sep.sep_backend.exercise.entity.ExerciseMcq;
import com.sep.sep_backend.exercise.repository.ExerciseFillBlankRepository;
import com.sep.sep_backend.exercise.repository.ExerciseMcqRepository;
import com.sep.sep_backend.user.entity.Language;
import com.sep.sep_backend.user.entity.LanguageLevel;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for generating pronunciation practice sentences from existing exercises.
 */
@Service
public class PracticeSentenceService {

    private final ExerciseFillBlankRepository fillBlankRepository;
    private final ExerciseMcqRepository mcqRepository;
    private final Random random = new Random();

    public PracticeSentenceService(
            ExerciseFillBlankRepository fillBlankRepository,
            ExerciseMcqRepository mcqRepository) {
        this.fillBlankRepository = fillBlankRepository;
        this.mcqRepository = mcqRepository;
    }

    /**
     * Fetches random practice sentences from exercises based on language and level.
     *
     * @param language Target language (DE, EN, etc.)
     * @param level    Difficulty level (A1, A2, B1, B2, C1, C2)
     * @param count    Number of sentences to return (default 5)
     * @return List of practice sentences
     */
    public List<PracticeSentenceResponse> getPracticeSentences(
            Language language,
            LanguageLevel level,
            int count) {

        List<PracticeSentenceResponse> sentences = new ArrayList<>();

        // Fetch from Fill Blank exercises
        Specification<ExerciseFillBlank> fillBlankSpec = (root, query, cb) ->
                cb.and(
                        cb.equal(root.get("targetLanguage"), language),
                        cb.equal(root.get("difficultyLevel"), level)
                );

        List<ExerciseFillBlank> fillBlanks = fillBlankRepository.findAll(fillBlankSpec);
        for (ExerciseFillBlank exercise : fillBlanks) {
            // Replace ___ with the correct answer to create a complete sentence
            String completeSentence = exercise.getSentenceWithBlank()
                    .replace("___", exercise.getCorrectAnswer());

            sentences.add(new PracticeSentenceResponse(
                    exercise.getId(),
                    completeSentence,
                    exercise.getDifficultyLevel(),
                    exercise.getTopic(),
                    exercise.getContentType() != null ? exercise.getContentType().name() : "VOCABULARY"
            ));
        }

        // Fetch from MCQ exercises
        Specification<ExerciseMcq> mcqSpec = (root, query, cb) ->
                cb.and(
                        cb.equal(root.get("targetLanguage"), language),
                        cb.equal(root.get("difficultyLevel"), level)
                );

        List<ExerciseMcq> mcqs = mcqRepository.findAll(mcqSpec);
        for (ExerciseMcq exercise : mcqs) {
            // Use question text as practice sentence if it's a complete sentence
            String questionText = exercise.getQuestionText();
            if (questionText != null && questionText.length() > 10) {
                sentences.add(new PracticeSentenceResponse(
                        exercise.getId(),
                        questionText,
                        exercise.getDifficultyLevel(),
                        exercise.getTopic(),
                        exercise.getContentType() != null ? exercise.getContentType().name() : "VOCABULARY"
                ));
            }
        }

        // Shuffle and return random subset
        Collections.shuffle(sentences, random);
        return sentences.stream()
                .limit(Math.min(count, sentences.size()))
                .collect(Collectors.toList());
    }

    /**
     * Validates if an exercise ID exists and returns its XP reward.
     *
     * @param exerciseId The UUID of the exercise
     * @return XP reward value, or null if not found
     */
    public Integer getExerciseXpReward(UUID exerciseId) {
        Optional<ExerciseFillBlank> fillBlank = fillBlankRepository.findById(exerciseId);
        if (fillBlank.isPresent()) {
            return fillBlank.get().getXpReward();
        }

        Optional<ExerciseMcq> mcq = mcqRepository.findById(exerciseId);
        if (mcq.isPresent()) {
            return mcq.get().getXpReward();
        }

        return null;
    }
}
