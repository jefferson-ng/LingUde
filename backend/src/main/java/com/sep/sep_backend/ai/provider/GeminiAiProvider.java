package com.sep.sep_backend.ai.provider;

import com.google.genai.Client;
import com.google.genai.types.*;
import com.sep.sep_backend.ai.config.GeminiConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Gemini AI provider implementation.
 * Handles communication with Google's Gemini API with retry logic and error handling.
 */
@Component
public class GeminiAiProvider implements AiProvider {

    private static final Logger log = LoggerFactory.getLogger(GeminiAiProvider.class);
    private static final int MAX_RETRIES = 3;

    private final Client geminiClient;
    private final GeminiConfig geminiConfig;

    public GeminiAiProvider(Client geminiClient, GeminiConfig geminiConfig) {
        this.geminiClient = geminiClient;
        this.geminiConfig = geminiConfig;
    }

    @Override
    public AiProviderResponse chat(
            List<Content> conversationHistory,
            List<FunctionDeclaration> availableTools,
            String systemPrompt) {

        log.debug("Calling Gemini API with {} tools", availableTools != null ? availableTools.size() : 0);

        String modelName = ensureModelPrefix(geminiConfig.getModel());

        // Build complete conversation with system prompt
        List<Content> allContents = new ArrayList<>();

        // Add system prompt as first message
        allContents.add(
            Content.builder()
                .role("user")
                .parts(List.of(Part.builder().text(systemPrompt).build()))
                .build()
        );

        // Add conversation history
        if (conversationHistory != null && !conversationHistory.isEmpty()) {
            allContents.addAll(conversationHistory);
        }

        // Configure generation with tools
        GenerateContentConfig config = null;
        if (availableTools != null && !availableTools.isEmpty()) {
            config = GenerateContentConfig.builder()
                .tools(List.of(Tool.builder().functionDeclarations(availableTools).build()))
                .build();
        }

        // Retry logic with exponential backoff
        int attempt = 0;
        Exception lastException = null;

        while (attempt < MAX_RETRIES) {
            try {
                GenerateContentResponse response = geminiClient.models.generateContent(modelName, allContents, config);

                if (hasValidContent(response)) {
                    log.debug("Received valid Gemini response (attempt {})", attempt + 1);
                    return convertToAiProviderResponse(response);
                }

                log.warn("Gemini returned empty response (attempt {}/{})", attempt + 1, MAX_RETRIES);

            } catch (Exception e) {
                lastException = e;
                log.error("Gemini API call failed (attempt {}/{}): {}", attempt + 1, MAX_RETRIES, e.getMessage());

                if (attempt == MAX_RETRIES - 1) {
                    throw new RuntimeException(
                        "AI service unavailable after " + MAX_RETRIES + " attempts: " + e.getMessage(), e);
                }
            }

            // Exponential backoff
            attempt++;
            if (attempt < MAX_RETRIES) {
                try {
                    long backoffMs = (long) Math.pow(2, attempt) * 1000;
                    Thread.sleep(backoffMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }
            }
        }

        throw new RuntimeException("AI service unavailable after " + MAX_RETRIES + " attempts", lastException);
    }

    /**
     * Check if Gemini response has valid content (text or function calls)
     */
    private boolean hasValidContent(GenerateContentResponse response) {
        if (response.candidates().isEmpty() || response.candidates().get().isEmpty()) {
            return false;
        }

        Candidate candidate = response.candidates().get().get(0);
        if (candidate.content().isEmpty()) {
            return false;
        }

        Content content = candidate.content().get();
        if (content.parts().isEmpty() || content.parts().get().isEmpty()) {
            return false;
        }

        // Check if any part has actual content
        for (Part part : content.parts().get()) {
            if (part.text().isPresent() && !part.text().get().isBlank()) {
                return true;
            }
            if (part.functionCall().isPresent()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Convert Gemini response to standardized AiProviderResponse format
     */
    private AiProviderResponse convertToAiProviderResponse(GenerateContentResponse response) {
        List<AiProviderResponse.ToolCall> toolCalls = new ArrayList<>();
        String text = null;
        boolean hasFunctionCalls = false;

        log.debug("Converting Gemini response - candidates present: {}", response.candidates().isPresent());

        if (response.candidates().isPresent() && !response.candidates().get().isEmpty()) {
            Candidate candidate = response.candidates().get().get(0);

            if (candidate.finishReason().isPresent()) {
                log.debug("Gemini finish reason: {}", candidate.finishReason().get());
            }

            if (candidate.content().isPresent()) {
                Content content = candidate.content().get();

                if (content.parts().isPresent()) {
                    List<Part> parts = content.parts().get();
                    log.debug("Gemini response has {} parts", parts.size());

                    for (Part part : parts) {
                        // Extract function calls
                        if (part.functionCall().isPresent()) {
                            hasFunctionCalls = true;
                            FunctionCall fc = part.functionCall().get();
                            String functionName = fc.name().orElse("unknown");
                            Map<String, Object> args = fc.args().orElse(new HashMap<>());
                            log.info("Gemini function call: {} with args: {}", functionName, args);

                            toolCalls.add(new AiProviderResponse.ToolCall(functionName, args));
                        }

                        // Extract text
                        if (part.text().isPresent()) {
                            text = part.text().get();
                            log.debug("Gemini text part found, length: {}", text.length());
                        }
                    }
                }
            }
        }

        // Fallback: try to get text from response
        if (!hasFunctionCalls && text == null) {
            text = response.text();
        }

        log.debug("Final AiProviderResponse - text: {}, hasFunctionCalls: {}, functionCallCount: {}",
            text != null ? "present" : "null", hasFunctionCalls, toolCalls.size());

        return new AiProviderResponse(text, toolCalls, hasFunctionCalls);
    }

    /**
     * Ensure model name has the correct "models/" prefix required by the Gen AI SDK
     */
    private String ensureModelPrefix(String modelName) {
        if (modelName == null || modelName.isEmpty()) {
            throw new IllegalArgumentException("Model name cannot be null or empty");
        }

        if (modelName.startsWith("models/")) {
            return modelName;
        }

        return "models/" + modelName;
    }

    @Override
    public String getProviderName() {
        return "Gemini";
    }

    @Override
    public boolean isAvailable() {
        return geminiConfig != null && geminiClient != null;
    }
}
