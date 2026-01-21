package com.sep.sep_backend.ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.types.Content;
import com.google.genai.types.FunctionCall;
import com.google.genai.types.FunctionResponse;
import com.google.genai.types.Part;
import com.sep.sep_backend.ai.entity.ChatMessage;
import com.sep.sep_backend.ai.entity.ChatSession;

import com.sep.sep_backend.ai.entity.ToolExecutionLog;
import com.sep.sep_backend.ai.provider.AiProvider;
import com.sep.sep_backend.ai.provider.AiProviderResponse;
import com.sep.sep_backend.ai.repository.ChatMessageRepository;
import com.sep.sep_backend.ai.repository.ChatSessionRepository;
import com.sep.sep_backend.ai.repository.ToolExecutionLogRepository;
import com.sep.sep_backend.ai.tool.AiToolService;
import com.sep.sep_backend.user.entity.Language;
import com.sep.sep_backend.user.entity.User;
import com.sep.sep_backend.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import com.sep.sep_backend.ai.dto.ConversationResult;

/**
 * Core conversation orchestration service.
 * Handles multi-turn conversations with tool calling support.
 */
@Service
@Transactional
public class ConversationService {

    private static final Logger log = LoggerFactory.getLogger(ConversationService.class);
    private static final int MAX_TOOL_ITERATIONS = 5;

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final ToolExecutionLogRepository toolLogRepository;
    private final AiProvider aiProvider;
    private final AiToolService toolService;
    private final SystemPromptService systemPromptService;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    public ConversationService(
            ChatSessionRepository sessionRepository,
            ChatMessageRepository messageRepository,
            ToolExecutionLogRepository toolLogRepository,
            AiProvider aiProvider,
            AiToolService toolService,
            SystemPromptService systemPromptService,
            ObjectMapper objectMapper,
            UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.toolLogRepository = toolLogRepository;
        this.aiProvider = aiProvider;
        this.toolService = toolService;
        this.systemPromptService = systemPromptService;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
    }

    /**
     * Get or create an active chat session for the user.
     *
     * @param userId User ID
     * @param learningLanguage User's learning language
     * @return Active chat session
     */
    public ChatSession getOrCreateSession(UUID userId, Language learningLanguage) {
        Optional<ChatSession> existingSession = sessionRepository
            .findFirstByUser_IdAndIsActiveTrueOrderByUpdatedAtDesc(userId);

        if (existingSession.isPresent()) {
            ChatSession session = existingSession.get();
            session.setUpdatedAt(LocalDateTime.now());
            log.debug("Using existing session: {}", session.getId());
            return sessionRepository.save(session);
        }

        // Create new session
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        ChatSession newSession = new ChatSession();
        newSession.setUser(user);
        newSession.setLearningLanguage(learningLanguage);
        newSession.setCreatedAt(LocalDateTime.now());
        newSession.setUpdatedAt(LocalDateTime.now());
        newSession.setIsActive(true);

        ChatSession saved = sessionRepository.save(newSession);
        log.info("Created new chat session: {} for user: {}", saved.getId(), userId);
        return saved;
    }

    /**
     * Send a user message and get AI response with multi-turn tool calling.
     *
     * @param userId User ID
     * @param sessionId Chat session ID
     * @param userMessage User's message text
     * @return ConversationResult containing the response and tool call info
     */
    public ConversationResult sendMessage(UUID userId, UUID sessionId, String userMessage) {
        log.info("Processing message for user: {}, session: {}", userId, sessionId);

        ConversationResult result = new ConversationResult();

        ChatSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

        // 1. Persist user message
        ChatMessage userMsg = new ChatMessage();
        userMsg.setSession(session);
        userMsg.setRole(ChatMessage.MessageRole.USER);
        userMsg.setContent(userMessage);
        userMsg.setCreatedAt(LocalDateTime.now());
        messageRepository.save(userMsg);

        // 2. Build conversation history from database
        List<Content> conversationHistory = buildConversationHistory(sessionId);

        // 3. Get system prompt and available tools
        String systemPrompt = systemPromptService.getSystemPrompt(userId);
        var availableTools = toolService.getAllFunctionDeclarations();

        log.debug("Starting AI conversation with {} tools available", availableTools.size());

        // 4. Multi-turn tool calling loop
        String finalResponse = null;
        int iteration = 0;

        while (iteration < MAX_TOOL_ITERATIONS) {
            iteration++;
            log.debug("AI iteration {}/{}", iteration, MAX_TOOL_ITERATIONS);

            // Call AI provider
            AiProviderResponse response = aiProvider.chat(
                conversationHistory,
                availableTools,
                systemPrompt
            );

            // If AI wants to call tools, execute them
            if (response.isHasToolCalls() && !response.getToolCalls().isEmpty()) {
                log.info("AI requested {} tool call(s)", response.getToolCalls().size());

                List<FunctionResponse> toolResults = executeToolCalls(
                    userId,
                    sessionId,
                    response.getToolCalls(),
                    result  // Pass result to capture tool call info
                );

                // Persist AI's tool call request
                persistToolCallMessage(session, response);

                // Persist tool results
                persistToolResultMessage(session, toolResults);

                // Add tool calls to conversation history for next iteration
                conversationHistory.add(buildToolCallContent(response.getToolCalls()));
                conversationHistory.add(buildToolResultContent(toolResults));

                // Continue loop to get AI's next response
                continue;
            }

            // AI returned text response (no more tool calls)
            if (response.getTextContent() != null && !response.getTextContent().isBlank()) {
                finalResponse = response.getTextContent();
                log.debug("AI returned final text response, length: {}", finalResponse.length());

                // Persist AI's final response
                ChatMessage aiMsg = new ChatMessage();
                aiMsg.setSession(session);
                aiMsg.setRole(ChatMessage.MessageRole.MODEL);
                aiMsg.setContent(finalResponse);
                aiMsg.setCreatedAt(LocalDateTime.now());
                messageRepository.save(aiMsg);

                break;
            }

            // Safety: if no tool calls and no text, break to avoid infinite loop
            log.warn("AI returned neither tool calls nor text, ending conversation");
            finalResponse = "I apologize, but I encountered an issue processing your request. Please try again.";
            break;
        }

        if (iteration >= MAX_TOOL_ITERATIONS) {
            log.warn("Reached max tool calling iterations ({})", MAX_TOOL_ITERATIONS);
            finalResponse = "I apologize, but this is taking longer than expected. Let's start fresh with your question.";
        }

        // Update session timestamp
        session.setUpdatedAt(LocalDateTime.now());
        sessionRepository.save(session);

        result.setResponse(finalResponse != null ? finalResponse : "I apologize, but I couldn't generate a response.");
        return result;
    }

    /**
     * Execute all tool calls requested by the AI.
     */
    private List<FunctionResponse> executeToolCalls(
            UUID userId,
            UUID sessionId,
            List<AiProviderResponse.ToolCall> toolCalls,
            ConversationResult conversationResult) {

        List<FunctionResponse> results = new ArrayList<>();

        for (AiProviderResponse.ToolCall toolCall : toolCalls) {
            String toolName = toolCall.getFunctionName();
            Map<String, Object> args = toolCall.getArguments();

            log.info("Executing tool: {} with args: {}", toolName, args);

            long startTime = System.currentTimeMillis();
            Object result;
            boolean success;
            String errorMessage = null;

            try {
                result = toolService.executeTool(toolName, userId, args);
                success = true;
                log.info("Tool {} executed successfully", toolName);
            } catch (Exception e) {
                log.error("Tool {} execution failed: {}", toolName, e.getMessage(), e);
                result = Map.of("error", e.getMessage());
                success = false;
                errorMessage = e.getMessage();
            }

            long executionTime = System.currentTimeMillis() - startTime;

            // Log tool execution for debugging/analytics
            logToolExecution(sessionId, toolName, args, result, executionTime, success, errorMessage);

            // Add to conversation result for frontend debug panel
            conversationResult.addToolCall(new ConversationResult.ToolCallInfo(
                toolName,
                args,
                result,
                executionTime
            ));

            // Build FunctionResponse for AI
            @SuppressWarnings("unchecked")
            Map<String, Object> resultMap = result instanceof Map ? (Map<String, Object>) result : Map.of("result", result);
            FunctionResponse functionResponse = FunctionResponse.builder()
                .name(toolName)
                .response(resultMap)
                .build();

            results.add(functionResponse);
        }

        return results;
    }

    /**
     * Log tool execution to database for debugging and analytics.
     */
    private void logToolExecution(
            UUID sessionId,
            String toolName,
            Map<String, Object> parameters,
            Object result,
            long executionTimeMs,
            boolean success,
            String errorMessage) {

        try {
            ChatSession session = sessionRepository.findById(sessionId).orElse(null);
            if (session == null) {
                log.warn("Session not found for tool execution log: {}", sessionId);
                return;
            }

            ToolExecutionLog logEntry = new ToolExecutionLog();
            logEntry.setSession(session);
            logEntry.setToolName(toolName);
            logEntry.setParameters(objectMapper.writeValueAsString(parameters));
            logEntry.setResult(objectMapper.writeValueAsString(result));
            logEntry.setExecutedAt(LocalDateTime.now());
            logEntry.setExecutionTimeMs((int) executionTimeMs);
            logEntry.setSuccess(success);
            logEntry.setErrorMessage(errorMessage);

            toolLogRepository.save(logEntry);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize tool execution log: {}", e.getMessage());
        }
    }

    /**
     * Persist AI's tool call request as a message.
     */
    private void persistToolCallMessage(ChatSession session, AiProviderResponse response) {
        try {
            ChatMessage msg = new ChatMessage();
            msg.setSession(session);
            msg.setRole(ChatMessage.MessageRole.MODEL);
            msg.setToolCalls(objectMapper.writeValueAsString(response.getToolCalls()));
            msg.setCreatedAt(LocalDateTime.now());
            messageRepository.save(msg);
        } catch (JsonProcessingException e) {
            log.error("Failed to persist tool call message: {}", e.getMessage());
        }
    }

    /**
     * Persist tool execution results as a message.
     */
    private void persistToolResultMessage(ChatSession session, List<FunctionResponse> results) {
        try {
            ChatMessage msg = new ChatMessage();
            msg.setSession(session);
            msg.setRole(ChatMessage.MessageRole.TOOL);
            msg.setToolResults(objectMapper.writeValueAsString(results));
            msg.setCreatedAt(LocalDateTime.now());
            messageRepository.save(msg);
        } catch (JsonProcessingException e) {
            log.error("Failed to persist tool result message: {}", e.getMessage());
        }
    }

    /**
     * Build conversation history from database messages.
     * Converts ChatMessage entities to Gemini Content objects.
     */
    private List<Content> buildConversationHistory(UUID sessionId) {
        List<ChatMessage> messages = messageRepository.findBySession_IdOrderByCreatedAtAsc(sessionId);
        List<Content> history = new ArrayList<>();

        log.debug("Building conversation history from {} messages", messages.size());

        for (ChatMessage msg : messages) {
            Content content = convertToContent(msg);
            if (content != null) {
                history.add(content);
            }
        }

        return history;
    }

    /**
     * Convert ChatMessage entity to Gemini Content object.
     */
    private Content convertToContent(ChatMessage message) {
        try {
            List<Part> parts = new ArrayList<>();

            // Handle text content
            if (message.getContent() != null && !message.getContent().isBlank()) {
                parts.add(Part.builder().text(message.getContent()).build());
            }

            // Handle tool calls (AI requesting tool execution)
            if (message.getToolCalls() != null && !message.getToolCalls().isBlank()) {
                @SuppressWarnings("unchecked")
                List<AiProviderResponse.ToolCall> toolCalls = (List<AiProviderResponse.ToolCall>) objectMapper.readValue(
                    message.getToolCalls(),
                    objectMapper.getTypeFactory().constructCollectionType(
                        List.class,
                        AiProviderResponse.ToolCall.class
                    )
                );

                for (AiProviderResponse.ToolCall tc : toolCalls) {
                    FunctionCall fc = FunctionCall.builder()
                        .name(tc.getFunctionName())
                        .args(tc.getArguments())
                        .build();
                    parts.add(Part.builder().functionCall(fc).build());
                }
            }

            // Handle tool results
            if (message.getToolResults() != null && !message.getToolResults().isBlank()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> toolResults = (List<Map<String, Object>>) objectMapper.readValue(
                    message.getToolResults(),
                    objectMapper.getTypeFactory().constructCollectionType(
                        List.class,
                        Map.class
                    )
                );

                for (Map<String, Object> result : toolResults) {
                    String name = (String) result.get("name");
                    Object response = result.get("response");

                    @SuppressWarnings("unchecked")
                    Map<String, Object> responseMap = response instanceof Map ? (Map<String, Object>) response : Map.of("result", response);
                    FunctionResponse fr = FunctionResponse.builder()
                        .name(name)
                        .response(responseMap)
                        .build();
                    parts.add(Part.builder().functionResponse(fr).build());
                }
            }

            if (parts.isEmpty()) {
                return null;
            }

            // Map MessageRole to Gemini role
            String role = switch (message.getRole()) {
                case USER -> "user";
                case MODEL -> "model";
                case TOOL -> "function";  // Gemini uses "function" for tool results
            };

            return Content.builder()
                .role(role)
                .parts(parts)
                .build();

        } catch (JsonProcessingException e) {
            log.error("Failed to convert message to Content: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Build Content object for tool calls (AI's request to execute tools).
     */
    private Content buildToolCallContent(List<AiProviderResponse.ToolCall> toolCalls) {
        List<Part> parts = new ArrayList<>();

        for (AiProviderResponse.ToolCall tc : toolCalls) {
            FunctionCall fc = FunctionCall.builder()
                .name(tc.getFunctionName())
                .args(tc.getArguments())
                .build();
            parts.add(Part.builder().functionCall(fc).build());
        }

        return Content.builder()
            .role("model")
            .parts(parts)
            .build();
    }

    /**
     * Build Content object for tool results (results of tool execution).
     */
    private Content buildToolResultContent(List<FunctionResponse> results) {
        List<Part> parts = new ArrayList<>();

        for (FunctionResponse fr : results) {
            parts.add(Part.builder().functionResponse(fr).build());
        }

        return Content.builder()
            .role("function")
            .parts(parts)
            .build();
    }

    /**
     * Close a chat session (mark as inactive).
     */
    public void closeSession(UUID sessionId, UUID userId) {
        ChatSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (!session.getUser().getId().equals(userId)) {
            throw new SecurityException("Unauthorized access to session");
        }

        session.setIsActive(false);
        session.setUpdatedAt(LocalDateTime.now());
        sessionRepository.save(session);

        log.info("Closed session: {} for user: {}", sessionId, userId);
    }

    /**
     * Get all sessions for a user.
     */
    public List<ChatSession> getUserSessions(UUID userId) {
        return sessionRepository.findByUser_IdOrderByUpdatedAtDesc(userId);
    }
}
