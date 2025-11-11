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

        List<String> options = new ArrayList<>(Arrays.asList(
                e.getCorrectAnswer(), e.getWrongOption1(), e.getWrongOption2(), e.getWrongOption3()
        ));
        Collections.shuffle(options);
        dto.setOptions(options);
        return dto;
    }

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

    @Transactional
    public SubmissionResultResponse submitMcq(UUID id, McqSubmissionRequest req, User user) {
        ExerciseMcq e = mcqRepo.findById(id).orElseThrow(() -> new NoSuchElementException("MCQ not found"));
        boolean correct = e.getCorrectAnswer().equals(req.getSelectedAnswer());
        int xp = correct ? e.getXpReward() : 0;
        upsertProgress(user, id, ExerciseType.MCQ, correct, xp);
        String feedback = correct ? "Great job!" : "Try again.";
        return new SubmissionResultResponse(correct, xp, e.getCorrectAnswer(), feedback);
        // NOTE: In production, you may omit correctAnswer from response to prevent leaks.
    }

    @Transactional
    public SubmissionResultResponse submitFillBlank(UUID id, FillBlankSubmissionRequest req, User user) {
        ExerciseFillBlank e = fillRepo.findById(id).orElseThrow(() -> new NoSuchElementException("Fill-Blank not found"));
        String userAns = normalize(req.getAnswerText());
        String sol = normalize(e.getCorrectAnswer());
        boolean correct = userAns.equals(sol);
        int xp = correct ? e.getXpReward() : 0;
        upsertProgress(user, id, ExerciseType.FILL_BLANK, correct, xp);
        String feedback = correct ? "Nice!" : "Remember the correct form.";
        return new SubmissionResultResponse(correct, xp, e.getCorrectAnswer(), feedback);
    }

    private String normalize(String s) {
        if (s == null) return "";
        return s.trim().toLowerCase().replaceAll("\\s+", " ");
    }

    private void upsertProgress(User user, UUID exerciseId, ExerciseType type, boolean correct, int xp) {
        if (user == null) return; // plug in your auth later
        UUID userId = user.getId();

        UserProgress existing = progressRepo
                .findByUserIdAndExerciseIdAndExerciseType(userId, exerciseId, type)
                .orElse(null);

        if (existing == null) {
            UserProgress up = new UserProgress(user, exerciseId, type);
            if (correct) {
                up.setIsCompleted(true);
                up.setCompletedAt(LocalDateTime.now());
                up.setXpEarned(xp);
            }
            progressRepo.save(up);
        } else {
            if (!Boolean.TRUE.equals(existing.getIsCompleted()) && correct) {
                existing.setIsCompleted(true);
                existing.setCompletedAt(LocalDateTime.now());
                existing.setXpEarned(xp);
                progressRepo.save(existing);
            }
        }
    }
}
