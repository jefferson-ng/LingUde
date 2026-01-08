package com.sep.sep_backend.ai.tool;

import com.google.genai.types.FunctionDeclaration;

import java.util.Map;
import java.util.UUID;

/**
 * Interface for AI tool/function handlers.
 * Each tool handler implements a specific function that the AI can call during conversation.
 */
public interface AiToolHandler {

    /**
     * Get the function declaration to provide to the AI.
     * This tells the AI what the function does, what parameters it needs, etc.
     *
     * @return Function declaration for the AI
     */
    FunctionDeclaration getFunctionDeclaration();

    /**
     * Execute the tool with the given parameters.
     *
     * @param userId Current authenticated user ID
     * @param parameters Parameters passed by the AI
     * @return Result object to send back to the AI
     */
    Object execute(UUID userId, Map<String, Object> parameters);

    /**
     * Get the unique name of this tool.
     * Must match the name in the function declaration.
     *
     * @return Tool name
     */
    String getToolName();
}
