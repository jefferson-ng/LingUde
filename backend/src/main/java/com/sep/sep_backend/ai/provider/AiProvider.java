package com.sep.sep_backend.ai.provider;

import com.google.genai.types.Content;
import com.google.genai.types.FunctionDeclaration;

import java.util.List;

/**
 * Interface for AI provider implementations.
 * Allows for multiple AI providers (Gemini, Ollama, etc.) with a unified interface.
 */
public interface AiProvider {

    /**
     * Send a chat request with conversation history and available tools.
     *
     * @param conversationHistory Previous messages in the conversation
     * @param availableTools List of tools/functions the AI can call
     * @param systemPrompt System instructions for the AI
     * @return AI response with text and/or tool calls
     */
    AiProviderResponse chat(
        List<Content> conversationHistory,
        List<FunctionDeclaration> availableTools,
        String systemPrompt
    );

    /**
     * Get the provider name for logging and identification.
     *
     * @return Provider name (e.g., "Gemini", "Ollama")
     */
    String getProviderName();

    /**
     * Check if the provider is available and configured.
     *
     * @return true if provider can be used
     */
    boolean isAvailable();
}
