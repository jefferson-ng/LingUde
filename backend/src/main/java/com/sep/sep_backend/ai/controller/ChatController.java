package com.sep.sep_backend.ai.controller;

import com.sep.sep_backend.ai.dto.ChatHistoryResponse;
import com.sep.sep_backend.ai.dto.ChatMessageRequest;
import com.sep.sep_backend.ai.dto.ChatMessageResponse;
import com.sep.sep_backend.ai.dto.ConversationResult;
import com.sep.sep_backend.ai.entity.ChatMessage;
import com.sep.sep_backend.ai.entity.ChatSession;
import com.sep.sep_backend.ai.repository.ChatMessageRepository;
import com.sep.sep_backend.ai.repository.ChatSessionRepository;
import com.sep.sep_backend.ai.service.ConversationService;
import com.sep.sep_backend.user.entity.Language;
import com.sep.sep_backend.user.entity.UserLearning;
import com.sep.sep_backend.user.service.UserLearningService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for AI-powered language learning chat.
 * Endpoints for sending messages, viewing history, and managing sessions.
 */
@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:4200")
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private final ConversationService conversationService;
    private final UserLearningService userLearningService;
    private final ChatMessageRepository messageRepository;
    private final ChatSessionRepository sessionRepository;

    public ChatController(
            ConversationService conversationService,
            UserLearningService userLearningService,
            ChatMessageRepository messageRepository,
            ChatSessionRepository sessionRepository) {
        this.conversationService = conversationService;
        this.userLearningService = userLearningService;
        this.messageRepository = messageRepository;
        this.sessionRepository = sessionRepository;
    }

    /**
     * POST /api/chat/message - Send message to AI tutor
     */
    @PostMapping("/message")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @Valid @RequestBody ChatMessageRequest request,
            Authentication authentication) {

        UUID userId = getCurrentUserId(authentication);
        log.info("Chat message received from user: {}", userId);

        // Get user's learning language
        Language learningLanguage = getLearningLanguage(userId);

        // Get or create session
        ChatSession session;
        if (request.getSessionId() != null && !request.getSessionId().isBlank()) {
            UUID sessionId = UUID.fromString(request.getSessionId());
            ChatSession existingSession = sessionRepository.findById(sessionId).orElse(null);
            if (existingSession == null) {
                return ResponseEntity.notFound().build();
            }
            if (!existingSession.getUser().getId().equals(userId)) {
                return ResponseEntity.status(403).build();
            }
            session = existingSession;
        } else {
            session = conversationService.getOrCreateSession(userId, learningLanguage);
        }

        // Send message and get AI response with tool call info
        ConversationResult conversationResult = conversationService.sendMessage(
            userId,
            session.getId(),
            request.getMessage()
        );

        // Get updated user stats
        Optional<UserLearning> learning = userLearningService.findLearningByUserId(userId);
        Integer currentXp = learning.map(UserLearning::getXp).orElse(0);
        Integer currentStreak = learning.map(UserLearning::getStreakCount).orElse(0);

        ChatMessageResponse response = new ChatMessageResponse(
            session.getId().toString(),
            conversationResult.getResponse(),
            LocalDateTime.now(),
            currentXp,
            currentStreak
        );
        response.setToolCalls(conversationResult.getToolCalls());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/chat/history/{sessionId} - Get chat history for a session
     */
    @GetMapping("/history/{sessionId}")
    public ResponseEntity<ChatHistoryResponse> getChatHistory(
            @PathVariable String sessionId,
            Authentication authentication) {

        UUID userId = getCurrentUserId(authentication);
        UUID sessionUuid = UUID.fromString(sessionId);

        // Get all messages for session
        List<ChatMessage> messages = messageRepository.findBySession_IdOrderByCreatedAtAsc(sessionUuid);

        if (messages.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Verify session belongs to user
        ChatMessage firstMessage = messages.get(0);
        if (!firstMessage.getSession().getUser().getId().equals(userId)) {
            return ResponseEntity.status(403).build();
        }

        // Convert to DTOs
        List<ChatHistoryResponse.MessageDto> messageDtos = messages.stream()
            .filter(msg -> msg.getContent() != null && !msg.getContent().isBlank())
            .map(msg -> new ChatHistoryResponse.MessageDto(
                msg.getRole().name(),
                msg.getContent(),
                msg.getCreatedAt()
            ))
            .collect(Collectors.toList());

        ChatHistoryResponse response = new ChatHistoryResponse(
            sessionId,
            firstMessage.getSession().getLearningLanguage().name(),
            messageDtos,
            firstMessage.getSession().getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/chat/sessions - Get all sessions for current user
     */
    @GetMapping("/sessions")
    public ResponseEntity<List<SessionSummaryDto>> getUserSessions(Authentication authentication) {
        UUID userId = getCurrentUserId(authentication);

        List<ChatSession> sessions = conversationService.getUserSessions(userId);

        List<SessionSummaryDto> summaries = sessions.stream()
            .map(session -> {
                long messageCount = messageRepository.countBySession_Id(session.getId());
                return new SessionSummaryDto(
                    session.getId().toString(),
                    session.getLearningLanguage().name(),
                    session.getCreatedAt(),
                    session.getUpdatedAt(),
                    session.getIsActive(),
                    (int) messageCount
                );
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(summaries);
    }

    /**
     * POST /api/chat/sessions/{sessionId}/close - Close/deactivate a session
     */
    @PostMapping("/sessions/{sessionId}/close")
    public ResponseEntity<Void> closeSession(
            @PathVariable String sessionId,
            Authentication authentication) {

        UUID userId = getCurrentUserId(authentication);
        UUID sessionUuid = UUID.fromString(sessionId);

        conversationService.closeSession(sessionUuid, userId);

        return ResponseEntity.ok().build();
    }

    /**
     * Extract user ID from JWT authentication
     */
    private UUID getCurrentUserId(Authentication authentication) {
        return UUID.fromString(authentication.getName());
    }

    /**
     * Get user's learning language or default to Spanish
     */
    private Language getLearningLanguage(UUID userId) {
        Optional<UserLearning> learning = userLearningService.findLearningByUserId(userId);
        return learning
            .map(UserLearning::getLearningLanguage)
            .orElse(Language.ES);  // Default to Spanish
    }

    /**
     * DTO for session summary in list view
     */
    public static class SessionSummaryDto {
        private String sessionId;
        private String learningLanguage;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Boolean isActive;
        private Integer messageCount;

        public SessionSummaryDto() {
        }

        public SessionSummaryDto(
                String sessionId,
                String learningLanguage,
                LocalDateTime createdAt,
                LocalDateTime updatedAt,
                Boolean isActive,
                Integer messageCount) {
            this.sessionId = sessionId;
            this.learningLanguage = learningLanguage;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.isActive = isActive;
            this.messageCount = messageCount;
        }

        // Getters and Setters
        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public String getLearningLanguage() {
            return learningLanguage;
        }

        public void setLearningLanguage(String learningLanguage) {
            this.learningLanguage = learningLanguage;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }

        public Boolean getIsActive() {
            return isActive;
        }

        public void setIsActive(Boolean isActive) {
            this.isActive = isActive;
        }

        public Integer getMessageCount() {
            return messageCount;
        }

        public void setMessageCount(Integer messageCount) {
            this.messageCount = messageCount;
        }
    }
}
