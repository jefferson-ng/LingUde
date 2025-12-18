package com.sep.sep_backend.exercise.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep.sep_backend.exercise.dto.ExerciseDetailResponse;
import com.sep.sep_backend.exercise.dto.ExerciseSummaryResponse;
import com.sep.sep_backend.exercise.dto.McqSubmissionRequest;
import com.sep.sep_backend.exercise.dto.SubmissionResultResponse;
import com.sep.sep_backend.exercise.entity.ExerciseType;
import com.sep.sep_backend.exercise.entity.UserProgress;
import com.sep.sep_backend.exercise.service.ExerciseService;
import com.sep.sep_backend.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.sep.sep_backend.auth.filter.JwtAuthenticationFilter;


import com.sep.sep_backend.exercise.dto.FillBlankSubmissionRequest;
import org.springframework.boot.CommandLineRunner;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;


/**
 * Controller-slice tests for ExerciseController.
 * Loads only the MVC layer and mocks ExerciseService.
 */



@WebMvcTest(controllers = ExerciseController.class)         // only load MVC layer for this controller
@AutoConfigureMockMvc(addFilters = false)                   //  disable all security filters in THIS test

class ExerciseControllerTest {

    @Resource
    private MockMvc mvc;              // performs HTTP calls against the controller

    @MockBean
    private ExerciseService service;  // mocked service layer

    @MockBean
    private UserRepository userRepository; //  satisfies SepBackendApplication.run(...)

    // This is the important part: override the CommandLineRunner bean
    @MockBean(name = "run")
    private CommandLineRunner commandLineRunner;

    // Mock JWT authentication filter so that SecurityConfig can be created in the test context
    // @MockBean tells Spring Boot Test to register a Mockito mock as a bean in the application context
    @MockBean
    // This mock instance will be injected into SecurityConfig's constructor instead of a real filter
    private JwtAuthenticationFilter jwtAuthenticationFilter;


    private final ObjectMapper om = new ObjectMapper(); // JSON (de)serializer

    /** GET /api/exercises/mcq -> list summaries */
    @Test
    void listMcq_returns200_andJsonArray() throws Exception {
        var s1 = new ExerciseSummaryResponse(
                UUID.randomUUID(), ExerciseType.MCQ, "EN", "A1",
                "Vocabulary", 10, "Choose the synonym of 'quick'");
        var s2 = new ExerciseSummaryResponse(
                UUID.randomUUID(), ExerciseType.MCQ, "EN", "A2",
                "Opposites", 12, "Pick the antonym of 'cold'");

        Mockito.when(service.listMcq()).thenReturn(List.of(s1, s2));

        mvc.perform(get("/api/exercises/mcq"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // minimal JSON checks
                .andExpect(jsonPath("$[0].type").value("MCQ"))
                .andExpect(jsonPath("$[0].previewText").value("Choose the synonym of 'quick'"))
                .andExpect(jsonPath("$[1].xpReward").value(12));
    }

    /** GET /api/exercises/mcq/{id} -> detail */
    @Test
    void getMcq_returns200_andDetail() throws Exception {
        var id = UUID.randomUUID();

        var d = new ExerciseDetailResponse();
        d.setId(id);
        d.setType(ExerciseType.MCQ);
        d.setTargetLanguage("EN");
        d.setDifficultyLevel("A1");
        d.setQuestionText("Synonym of 'happy'?");
        d.setOptions(List.of("glad", "sad", "late", "tiny"));
        d.setXpReward(15);

        Mockito.when(service.getMcq(eq(id))).thenReturn(d);

        mvc.perform(get("/api/exercises/mcq/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type").value("MCQ"))
                .andExpect(jsonPath("$.questionText").value("Synonym of 'happy'?"))
                .andExpect(jsonPath("$.options.length()").value(4))
                .andExpect(jsonPath("$.xpReward").value(15));
    }

    /** POST /api/exercises/mcq/{id}/submit -> submission result */
    @Test
    void submitMcq_returns200_andResult() throws Exception {
        var id = UUID.randomUUID();
        var body = new McqSubmissionRequest();
        body.setSelectedAnswer("glad");

        var result = new SubmissionResultResponse(true, 15, "glad", "Great job!");
        Mockito.when(service.submitMcq(eq(id), any(McqSubmissionRequest.class), any()))
                .thenReturn(result);

        mvc.perform(post("/api/exercises/mcq/{id}/submit", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.correct").value(true))
                .andExpect(jsonPath("$.xpEarned").value(15))   // <-- change here
                .andExpect(jsonPath("$.feedback").value("Great job!"));
    }

    // 1) GET /api/exercises/fillblank  -> list summaries
    @org.junit.jupiter.api.Test
    void listFillBlank_returns200_andJsonArray() throws Exception {
        var s1 = new ExerciseSummaryResponse(
                java.util.UUID.randomUUID(), ExerciseType.FILL_BLANK, "EN", "A1",
                "Grammar", 10, "I ___ football on Sundays.");
        var s2 = new ExerciseSummaryResponse(
                java.util.UUID.randomUUID(), ExerciseType.FILL_BLANK, "EN", "A2",
                "Daily life", 12, "They ___ breakfast at 8.");

        Mockito.when(service.listFillBlank()).thenReturn(java.util.List.of(s1, s2));

        mvc.perform(get("/api/exercises/fillblank"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].type").value("FILL_BLANK"))
                .andExpect(jsonPath("$[0].previewText").value("I ___ football on Sundays."))
                .andExpect(jsonPath("$[1].xpReward").value(12));
    }

    // 2) GET /api/exercises/fillblank/{id}  -> detail
    @org.junit.jupiter.api.Test
    void getFillBlank_returns200_andDetail() throws Exception {
        var id = java.util.UUID.randomUUID();
        var d = new ExerciseDetailResponse();
        d.setId(id);
        d.setType(ExerciseType.FILL_BLANK);
        d.setTargetLanguage("EN");
        d.setDifficultyLevel("A1");
        d.setSentenceWithBlank("She ___ to school.");
        d.setXpReward(11);
        d.setTopic("Present Simple");

        Mockito.when(service.getFillBlank(org.mockito.ArgumentMatchers.eq(id))).thenReturn(d);

        mvc.perform(get("/api/exercises/fillblank/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type").value("FILL_BLANK"))
                .andExpect(jsonPath("$.sentenceWithBlank").value("She ___ to school."))
                .andExpect(jsonPath("$.xpReward").value(11))
                .andExpect(jsonPath("$.difficultyLevel").value("A1"));
    }

    // 3) POST /api/exercises/fillblank/{id}/submit  -> submit result
    @org.junit.jupiter.api.Test
    void submitFillBlank_returns200_andResult() throws Exception {
        var id = java.util.UUID.randomUUID();
        var body = new FillBlankSubmissionRequest();
        body.setAnswerText("goes");

        var result = new SubmissionResultResponse(true, 12, "goes", "Nice!");
        Mockito.when(service.submitFillBlank(
                org.mockito.ArgumentMatchers.eq(id),
                org.mockito.ArgumentMatchers.any(FillBlankSubmissionRequest.class),
                org.mockito.ArgumentMatchers.any())
        ).thenReturn(result);

        mvc.perform(post("/api/exercises/fillblank/{id}/submit", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.correct").value(true))
                .andExpect(jsonPath("$.xpEarned").value(12))   // matches your DTO field
                .andExpect(jsonPath("$.feedback").value("Nice!"));
    }

    /** GET /api/exercises/completed -> list of completed exercises as DTOs */
    @Test
    void getCompletedExercisesForUser_returns200_andDtoList() throws Exception {
        // Given: two completed progress entries
        UUID ex1 = UUID.randomUUID();
        UUID ex2 = UUID.randomUUID();

        UserProgress up1 = new UserProgress();
        up1.setExerciseId(ex1);
        up1.setExerciseType(ExerciseType.MCQ);
        up1.setXpEarned(10);

        UserProgress up2 = new UserProgress();
        up2.setExerciseId(ex2);
        up2.setExerciseType(ExerciseType.FILL_BLANK);
        up2.setXpEarned(15);

        // Service returns these entries for the (here: null) userId
        Mockito.when(service.getCompletedExercisesForUser(null))
                .thenReturn(List.of(up1, up2));

        // When & Then: call endpoint and verify JSON
        mvc.perform(get("/api/exercises/completed"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].exerciseId").value(ex1.toString()))
                .andExpect(jsonPath("$[0].type").value("MCQ"))
                .andExpect(jsonPath("$[0].xpEarned").value(10))
                .andExpect(jsonPath("$[1].exerciseId").value(ex2.toString()))
                .andExpect(jsonPath("$[1].type").value("FILL_BLANK"))
                .andExpect(jsonPath("$[1].xpEarned").value(15));

        // Verify that the controller asked the service with userId = null
        Mockito.verify(service).getCompletedExercisesForUser(null);
    }

    /** GET /api/exercises/{exerciseId}/completed -> completion status DTO */
    @Test
    void hasUserCompletedExercise_returns200_andStatusDto() throws Exception {
        // Given
        UUID exerciseId = UUID.randomUUID();

        // Service says: this exercise is completed for this (null) user
        Mockito.when(service.hasUserCompletedExercise(null, exerciseId, ExerciseType.MCQ))
                .thenReturn(true);

        // When & Then
        mvc.perform(get("/api/exercises/{exerciseId}/completed", exerciseId)
                        .param("type", "MCQ"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exerciseId").value(exerciseId.toString()))
                .andExpect(jsonPath("$.type").value("MCQ"))
                .andExpect(jsonPath("$.completed").value(true));

        // Verify service call
        Mockito.verify(service).hasUserCompletedExercise(null, exerciseId, ExerciseType.MCQ);
    }



}
