package com.sep.sep_backend.exercise.service;

// ===== Imports for our app classes =====
import com.sep.sep_backend.exercise.dto.ExerciseDetailResponse;
import com.sep.sep_backend.exercise.entity.ExerciseMcq;
import com.sep.sep_backend.exercise.entity.ExerciseFillBlank;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import com.sep.sep_backend.exercise.repository.ExerciseFillBlankRepository;
import com.sep.sep_backend.exercise.repository.ExerciseMcqRepository;
import com.sep.sep_backend.exercise.repository.UserProgressRepository;

// ===== Imports for testing (JUnit 5 + Mockito + AssertJ) =====
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

// --- extra imports needed at the top ---
import com.sep.sep_backend.exercise.entity.UserProgress;
import com.sep.sep_backend.exercise.entity.ExerciseType;
import com.sep.sep_backend.user.entity.User;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;
import java.util.Optional;
import java.util.UUID;
import java.lang.reflect.Field;


/**
 * Unit tests for ExerciseService.
 * We mock repositories so NO real database is used.
 */
@ExtendWith(MockitoExtension.class) // Enable Mockito in JUnit 5
class ExerciseServiceTest {

    // Mocked repositories (fake DB layer)
    @Mock private ExerciseMcqRepository mcqRepo;
    @Mock private ExerciseFillBlankRepository fillRepo;
    @Mock private UserProgressRepository progressRepo;

    // Service under test, with mocks injected automatically
    @InjectMocks
    private ExerciseService service;

    /**
     * 1
     * Verifies that getMcq(UUID) returns the question and all 4 options.
     * Also sets targetLanguage / difficulty / xpReward to avoid NullPointerExceptions.
     */
    @Test
    void getMcq_returnsDetails_andIncludesAllOptions() {
        // 1) Given: a random exercise id and a fake MCQ entity
        UUID id = UUID.randomUUID();

        ExerciseMcq mcq = new ExerciseMcq();
        mcq.setQuestionText("Pick the synonym of 'beautiful'");
        mcq.setCorrectAnswer("pretty");
        mcq.setWrongOption1("ugly");
        mcq.setWrongOption2("late");
        mcq.setWrongOption3("small");

        // IMPORTANT: set these so the service doesn't hit nulls
        mcq.setXpReward(10);
        // Adjust enum values below if your Language/LanguageLevel names differ
        mcq.setTargetLanguage(com.sep.sep_backend.user.entity.Language.EN);
        mcq.setDifficultyLevel(com.sep.sep_backend.user.entity.LanguageLevel.A1);

        // And make the repository return our fake MCQ when asked
        when(mcqRepo.findById(id)).thenReturn(Optional.of(mcq));

        // 2) When: calling the service
        ExerciseDetailResponse dto = service.getMcq(id);

        // 3) Then: verify the response content
        assertThat(dto.getQuestionText()).isEqualTo("Pick the synonym of 'beautiful'");
        assertThat(dto.getOptions()).hasSize(4);
        assertThat(dto.getOptions())
                .containsExactlyInAnyOrder("pretty", "ugly", "late", "small"); // order can be shuffled
    }

    /**
     * TEST 2 — listMcq()
     * Ensures listMcq() returns summary DTOs correctly mapped from MCQ entities.
     */
    @org.junit.jupiter.api.Test
    void listMcq_returnsSummaryDtos_mappedFromEntities() {
        // 1) Arrange — two fake MCQs
        com.sep.sep_backend.exercise.entity.ExerciseMcq e1 = new com.sep.sep_backend.exercise.entity.ExerciseMcq();
        e1.setQuestionText("Choose the synonym of 'quick'");
        e1.setCorrectAnswer("fast");
        e1.setWrongOption1("slow");
        e1.setWrongOption2("late");
        e1.setWrongOption3("small");
        e1.setXpReward(10);
        e1.setTopic("Vocabulary");
        e1.setTargetLanguage(com.sep.sep_backend.user.entity.Language.EN);
        e1.setDifficultyLevel(com.sep.sep_backend.user.entity.LanguageLevel.A1);

        com.sep.sep_backend.exercise.entity.ExerciseMcq e2 = new com.sep.sep_backend.exercise.entity.ExerciseMcq();
        e2.setQuestionText("Pick the antonym of 'cold'");
        e2.setCorrectAnswer("hot");
        e2.setWrongOption1("cool");
        e2.setWrongOption2("freezing");
        e2.setWrongOption3("icy");
        e2.setXpReward(12);
        e2.setTopic("Opposites");
        e2.setTargetLanguage(com.sep.sep_backend.user.entity.Language.EN);
        e2.setDifficultyLevel(com.sep.sep_backend.user.entity.LanguageLevel.A2);

        // 2) Mock repository
        org.mockito.Mockito.when(mcqRepo.findAll()).thenReturn(java.util.List.of(e1, e2));

        // 3) Act
        java.util.List<com.sep.sep_backend.exercise.dto.ExerciseSummaryResponse> result = service.listMcq();

        // 4) Assert
        org.assertj.core.api.Assertions.assertThat(result).hasSize(2);

        com.sep.sep_backend.exercise.dto.ExerciseSummaryResponse first = result.get(0);
        org.assertj.core.api.Assertions.assertThat(first.getType())
                .isEqualTo(com.sep.sep_backend.exercise.entity.ExerciseType.MCQ);
        org.assertj.core.api.Assertions.assertThat(first.getPreviewText()).isEqualTo("Choose the synonym of 'quick'");
        org.assertj.core.api.Assertions.assertThat(first.getXpReward()).isEqualTo(10);
        org.assertj.core.api.Assertions.assertThat(first.getTargetLanguage()).isEqualTo("EN");
        org.assertj.core.api.Assertions.assertThat(first.getDifficultyLevel()).isEqualTo("A1");
        org.assertj.core.api.Assertions.assertThat(first.getTopic()).isEqualTo("Vocabulary");

        com.sep.sep_backend.exercise.dto.ExerciseSummaryResponse second = result.get(1);
        org.assertj.core.api.Assertions.assertThat(second.getType())
                .isEqualTo(com.sep.sep_backend.exercise.entity.ExerciseType.MCQ);
        org.assertj.core.api.Assertions.assertThat(second.getPreviewText()).isEqualTo("Pick the antonym of 'cold'");
        org.assertj.core.api.Assertions.assertThat(second.getXpReward()).isEqualTo(12);
        org.assertj.core.api.Assertions.assertThat(second.getTargetLanguage()).isEqualTo("EN");
        org.assertj.core.api.Assertions.assertThat(second.getDifficultyLevel()).isEqualTo("A2");
        org.assertj.core.api.Assertions.assertThat(second.getTopic()).isEqualTo("Opposites");
    } /**
     * ✅ Helper: set private field (e.g., id) via reflection if there is no setter.
     * This avoids depending on your entity having a public setId().
     */
    private static void setPrivate(Object target, String field, Object value) {
        try {
            Field f = target.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * ✅ TEST 3A — submitMcq: correct answer should award XP and mark progress completed.
     */
    @org.junit.jupiter.api.Test
    void submitMcq_correctAnswer_awardsXp_and_marksCompleted() {
        // Arrange: MCQ with known correct answer and XP
        UUID exId = UUID.randomUUID();
        com.sep.sep_backend.exercise.entity.ExerciseMcq mcq = new com.sep.sep_backend.exercise.entity.ExerciseMcq();
        mcq.setQuestionText("Synonym of 'happy'?");
        mcq.setCorrectAnswer("glad");
        mcq.setWrongOption1("sad");
        mcq.setWrongOption2("late");
        mcq.setWrongOption3("tiny");
        mcq.setXpReward(15);

        when(mcqRepo.findById(exId)).thenReturn(Optional.of(mcq));

        // No existing progress for this user/exercise/type
        when(progressRepo.findByUserIdAndExerciseIdAndExerciseType(any(), any(), any()))
                .thenReturn(Optional.empty());

        // A minimal User with an id (service only needs getId())
        User user = new User();
        try {
            // If your User has setId(UUID) you can call it instead.
            setPrivate(user, "id", UUID.randomUUID());
        } catch (RuntimeException ignore) { /* fine */ }

        // Build submission DTO
        com.sep.sep_backend.exercise.dto.McqSubmissionRequest req =
                new com.sep.sep_backend.exercise.dto.McqSubmissionRequest();
        req.setSelectedAnswer("glad"); // correct

        // Act
        com.sep.sep_backend.exercise.dto.SubmissionResultResponse result =
                service.submitMcq(exId, req, user);

        // Assert (repository side): we saved one new progress marked completed with XP=15
        ArgumentCaptor<UserProgress> captor = ArgumentCaptor.forClass(UserProgress.class);
        verify(progressRepo, times(1)).save(captor.capture());

        UserProgress saved = captor.getValue();
        org.assertj.core.api.Assertions.assertThat(saved.getExerciseType()).isEqualTo(ExerciseType.MCQ);
        org.assertj.core.api.Assertions.assertThat(saved.getIsCompleted()).isTrue();
        org.assertj.core.api.Assertions.assertThat(saved.getXpEarned()).isEqualTo(15);
        org.assertj.core.api.Assertions.assertThat(saved.getCompletedAt()).isNotNull();

        // (Optional) If your response has getters, you can also assert:
        // org.assertj.core.api.Assertions.assertThat(result.getCorrect()).isTrue();
        // org.assertj.core.api.Assertions.assertThat(result.getXp()).isEqualTo(15);
    }

    /**
     * ✅ TEST 3B — submitMcq: wrong answer gives XP=0 and keeps progress not completed.
     */
    @org.junit.jupiter.api.Test
    void submitMcq_wrongAnswer_awardsZero_and_keepsNotCompleted() {
        // Arrange
        UUID exId = UUID.randomUUID();
        com.sep.sep_backend.exercise.entity.ExerciseMcq mcq = new com.sep.sep_backend.exercise.entity.ExerciseMcq();
        mcq.setQuestionText("Synonym of 'large'?");
        mcq.setCorrectAnswer("big");
        mcq.setWrongOption1("small");
        mcq.setWrongOption2("thin");
        mcq.setWrongOption3("low");
        mcq.setXpReward(20);

        when(mcqRepo.findById(exId)).thenReturn(Optional.of(mcq));
        when(progressRepo.findByUserIdAndExerciseIdAndExerciseType(any(), any(), any()))
                .thenReturn(Optional.empty());

        User user = new User();
        try {
            setPrivate(user, "id", UUID.randomUUID());
        } catch (RuntimeException ignore) { /* fine */ }

        com.sep.sep_backend.exercise.dto.McqSubmissionRequest req =
                new com.sep.sep_backend.exercise.dto.McqSubmissionRequest();
        req.setSelectedAnswer("small"); // wrong

        // Act
        com.sep.sep_backend.exercise.dto.SubmissionResultResponse result =
                service.submitMcq(exId, req, user);

        // Assert
        ArgumentCaptor<UserProgress> captor = ArgumentCaptor.forClass(UserProgress.class);
        verify(progressRepo, times(1)).save(captor.capture());

        UserProgress saved = captor.getValue();
        org.assertj.core.api.Assertions.assertThat(saved.getExerciseType()).isEqualTo(ExerciseType.MCQ);
        org.assertj.core.api.Assertions.assertThat(saved.getIsCompleted()).isFalse(); // stays not completed
        org.assertj.core.api.Assertions.assertThat(saved.getXpEarned()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(saved.getCompletedAt()).isNull();

    }
    // -------------------------------------------
// TEST 4A — getFillBlank(): maps entity -> detail DTO
// -------------------------------------------
    @org.junit.jupiter.api.Test
    void getFillBlank_returnsDetailDto_mappedFromEntity() {
        // Arrange
        java.util.UUID id = java.util.UUID.randomUUID();
        ExerciseFillBlank fb = new ExerciseFillBlank();
        fb.setSentenceWithBlank("I ___ football on Sundays.");
        fb.setCorrectAnswer("play");
        fb.setXpReward(10);
        fb.setTopic("Present Simple");
        fb.setTargetLanguage(com.sep.sep_backend.user.entity.Language.EN);
        fb.setDifficultyLevel(com.sep.sep_backend.user.entity.LanguageLevel.A1);

        org.mockito.Mockito.when(fillRepo.findById(id)).thenReturn(java.util.Optional.of(fb));

        // Act
        com.sep.sep_backend.exercise.dto.ExerciseDetailResponse dto = service.getFillBlank(id);

        // Assert
        org.assertj.core.api.Assertions.assertThat(dto.getType())
                .isEqualTo(com.sep.sep_backend.exercise.entity.ExerciseType.FILL_BLANK);
        org.assertj.core.api.Assertions.assertThat(dto.getSentenceWithBlank()).isEqualTo("I ___ football on Sundays.");
        org.assertj.core.api.Assertions.assertThat(dto.getXpReward()).isEqualTo(10);
        org.assertj.core.api.Assertions.assertThat(dto.getTargetLanguage()).isEqualTo("EN");
        org.assertj.core.api.Assertions.assertThat(dto.getDifficultyLevel()).isEqualTo("A1");
        org.assertj.core.api.Assertions.assertThat(dto.getTopic()).isEqualTo("Present Simple");
    }

    // ------------------------------------------------------
// TEST 4B — submitFillBlank(): correct (trim/case tolerant)
// ------------------------------------------------------
    @org.junit.jupiter.api.Test
    void submitFillBlank_correctAnswer_marksCompleted_andAwardsXp() {
        // Arrange
        java.util.UUID exId = java.util.UUID.randomUUID();
        ExerciseFillBlank fb = new ExerciseFillBlank();
        fb.setSentenceWithBlank("She ___ to school.");
        fb.setCorrectAnswer("goes");
        fb.setXpReward(12);

        org.mockito.Mockito.when(fillRepo.findById(exId)).thenReturn(java.util.Optional.of(fb));
        org.mockito.Mockito.when(progressRepo.findByUserIdAndExerciseIdAndExerciseType(
                        org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(java.util.Optional.empty());

        // Minimal user with id
        com.sep.sep_backend.user.entity.User user = new com.sep.sep_backend.user.entity.User();
        try { setPrivate(user, "id", java.util.UUID.randomUUID()); } catch (RuntimeException ignored) {}

        // Note the spaces and casing — service should normalize
        com.sep.sep_backend.exercise.dto.FillBlankSubmissionRequest req =
                new com.sep.sep_backend.exercise.dto.FillBlankSubmissionRequest();
        req.setAnswerText("   GOES   ");

        // Act
        com.sep.sep_backend.exercise.dto.SubmissionResultResponse result =
                service.submitFillBlank(exId, req, user);

        // Assert saved progress
        org.mockito.ArgumentCaptor<com.sep.sep_backend.exercise.entity.UserProgress> cap =
                org.mockito.ArgumentCaptor.forClass(com.sep.sep_backend.exercise.entity.UserProgress.class);
        verify(progressRepo, times(1)).save(cap.capture());

        com.sep.sep_backend.exercise.entity.UserProgress saved = cap.getValue();
        org.assertj.core.api.Assertions.assertThat(saved.getExerciseType())
                .isEqualTo(com.sep.sep_backend.exercise.entity.ExerciseType.FILL_BLANK);
        org.assertj.core.api.Assertions.assertThat(saved.getIsCompleted()).isTrue();
        org.assertj.core.api.Assertions.assertThat(saved.getXpEarned()).isEqualTo(12);
        org.assertj.core.api.Assertions.assertThat(saved.getCompletedAt()).isNotNull();

    }

    // ------------------------------------------------------
// TEST 4C — submitFillBlank(): wrong answer gives XP=0
// ------------------------------------------------------
    @org.junit.jupiter.api.Test
    void submitFillBlank_wrongAnswer_keepsNotCompleted_andAwardsZero() {
        // Arrange
        java.util.UUID exId = java.util.UUID.randomUUID();
        ExerciseFillBlank fb = new ExerciseFillBlank();
        fb.setSentenceWithBlank("They ___ breakfast at 8.");
        fb.setCorrectAnswer("have");
        fb.setXpReward(9);

        org.mockito.Mockito.when(fillRepo.findById(exId)).thenReturn(java.util.Optional.of(fb));
        org.mockito.Mockito.when(progressRepo.findByUserIdAndExerciseIdAndExerciseType(
                        org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(java.util.Optional.empty());

        com.sep.sep_backend.user.entity.User user = new com.sep.sep_backend.user.entity.User();
        try { setPrivate(user, "id", java.util.UUID.randomUUID()); } catch (RuntimeException ignored) {}

        com.sep.sep_backend.exercise.dto.FillBlankSubmissionRequest req =
                new com.sep.sep_backend.exercise.dto.FillBlankSubmissionRequest();
        req.setAnswerText("eat"); // wrong

        // Act
        com.sep.sep_backend.exercise.dto.SubmissionResultResponse result =
                service.submitFillBlank(exId, req, user);

        // Assert
        org.mockito.ArgumentCaptor<com.sep.sep_backend.exercise.entity.UserProgress> cap =
                org.mockito.ArgumentCaptor.forClass(com.sep.sep_backend.exercise.entity.UserProgress.class);
        verify(progressRepo, times(1)).save(cap.capture());

        com.sep.sep_backend.exercise.entity.UserProgress saved = cap.getValue();
        org.assertj.core.api.Assertions.assertThat(saved.getExerciseType())
                .isEqualTo(com.sep.sep_backend.exercise.entity.ExerciseType.FILL_BLANK);
        org.assertj.core.api.Assertions.assertThat(saved.getIsCompleted()).isFalse();
        org.assertj.core.api.Assertions.assertThat(saved.getXpEarned()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(saved.getCompletedAt()).isNull();

    }
}
