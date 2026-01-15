package com.sep.sep_backend.ai.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for AI tutor chat messages.
 * Includes the AI's response plus updated user stats.
 */
public class ChatMessageResponse {

    private String sessionId;
    private String response;
    private LocalDateTime timestamp;
    private Integer currentXp;
    private Integer currentStreak;
    private List<ConversationResult.ToolCallInfo> toolCalls;

    public ChatMessageResponse() {
    }

    public ChatMessageResponse(
            String sessionId,
            String response,
            LocalDateTime timestamp,
            Integer currentXp,
            Integer currentStreak) {
        this.sessionId = sessionId;
        this.response = response;
        this.timestamp = timestamp;
        this.currentXp = currentXp;
        this.currentStreak = currentStreak;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getCurrentXp() {
        return currentXp;
    }

    public void setCurrentXp(Integer currentXp) {
        this.currentXp = currentXp;
    }

    public Integer getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(Integer currentStreak) {
        this.currentStreak = currentStreak;
    }

    public List<ConversationResult.ToolCallInfo> getToolCalls() {
        return toolCalls;
    }

    public void setToolCalls(List<ConversationResult.ToolCallInfo> toolCalls) {
        this.toolCalls = toolCalls;
    }
}
