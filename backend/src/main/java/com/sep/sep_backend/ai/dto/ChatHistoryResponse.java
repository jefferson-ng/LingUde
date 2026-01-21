package com.sep.sep_backend.ai.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Response DTO for chat history.
 * Contains all messages for a session.
 */
public class ChatHistoryResponse {

    private String sessionId;
    private String learningLanguage;
    private List<MessageDto> messages;
    private LocalDateTime createdAt;

    public ChatHistoryResponse() {
        this.messages = new ArrayList<>();
    }

    public ChatHistoryResponse(
            String sessionId,
            String learningLanguage,
            List<MessageDto> messages,
            LocalDateTime createdAt) {
        this.sessionId = sessionId;
        this.learningLanguage = learningLanguage;
        this.messages = messages != null ? messages : new ArrayList<>();
        this.createdAt = createdAt;
    }

    /**
     * Represents a single message in the conversation
     */
    public static class MessageDto {
        private String role;  // USER, MODEL, TOOL
        private String content;
        private LocalDateTime timestamp;

        public MessageDto() {
        }

        public MessageDto(String role, String content, LocalDateTime timestamp) {
            this.role = role;
            this.content = content;
            this.timestamp = timestamp;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }
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

    public List<MessageDto> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageDto> messages) {
        this.messages = messages;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
