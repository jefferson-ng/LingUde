package com.sep.sep_backend.ai.tool;

import com.google.genai.types.FunctionDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing and executing AI tools.
 * Auto-discovers all tool handlers via Spring DI and routes function calls to appropriate handlers.
 */
@Service
public class AiToolService {

    private static final Logger log = LoggerFactory.getLogger(AiToolService.class);

    private final Map<String, AiToolHandler> toolHandlers;

    /**
     * Constructor with auto-injection of all AiToolHandler beans.
     * Spring will automatically inject all implementations of AiToolHandler.
     */
    public AiToolService(List<AiToolHandler> handlers) {
        this.toolHandlers = handlers.stream()
            .collect(Collectors.toMap(
                AiToolHandler::getToolName,
                handler -> handler
            ));

        log.info("Registered {} AI tools: {}", toolHandlers.size(), toolHandlers.keySet());
    }

    /**
     * Get all function declarations for the AI.
     * These tell the AI what tools are available and how to use them.
     *
     * @return List of function declarations
     */
    public List<FunctionDeclaration> getAllFunctionDeclarations() {
        return toolHandlers.values().stream()
            .map(AiToolHandler::getFunctionDeclaration)
            .collect(Collectors.toList());
    }

    /**
     * Execute a specific tool by name.
     *
     * @param toolName Name of the tool to execute
     * @param userId Current user ID
     * @param parameters Parameters for the tool
     * @return Tool execution result
     * @throws IllegalArgumentException if tool doesn't exist
     */
    public Object executeTool(String toolName, UUID userId, Map<String, Object> parameters) {
        AiToolHandler handler = toolHandlers.get(toolName);

        if (handler == null) {
            log.warn("Unknown tool requested: {}", toolName);
            throw new IllegalArgumentException("Unknown tool: " + toolName);
        }

        log.info("Executing tool: {} for user: {}", toolName, userId);
        return handler.execute(userId, parameters);
    }

    /**
     * Check if a tool exists.
     *
     * @param toolName Tool name to check
     * @return true if tool is registered
     */
    public boolean hasTool(String toolName) {
        return toolHandlers.containsKey(toolName);
    }

    /**
     * Get the number of registered tools.
     *
     * @return Tool count
     */
    public int getToolCount() {
        return toolHandlers.size();
    }
}
