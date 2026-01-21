package com.sep.sep_backend.ai.tool;

import com.google.genai.types.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

/**
 * Abstract base class for AI tool handlers.
 * Provides common functionality: parameter extraction, schema builders, error handling.
 */
public abstract class AbstractAiToolHandler implements AiToolHandler {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public final Object execute(UUID userId, Map<String, Object> parameters) {
        log.info("Executing tool: {} for user: {}", getToolName(), userId);

        try {
            validateParameters(parameters);
            Object result = executeInternal(userId, parameters);
            log.info("Tool {} completed successfully", getToolName());
            return result;

        } catch (Exception e) {
            log.error("Tool {} failed: {}", getToolName(), e.getMessage(), e);
            return Map.of(
                "success", false,
                "error", e.getMessage()
            );
        }
    }

    /**
     * Validate parameters before execution.
     * Override in subclasses if validation is needed.
     */
    protected void validateParameters(Map<String, Object> parameters) {
        // Default: no validation
    }

    /**
     * Actual tool execution logic.
     * Subclasses implement this method with their specific logic.
     */
    protected abstract Object executeInternal(UUID userId, Map<String, Object> parameters);

    // ========== Parameter Extraction Helpers ==========

    /**
     * Safely extract String parameter
     */
    protected String getString(Map<String, Object> args, String key, String defaultValue) {
        if (args == null) return defaultValue;
        Object value = args.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    /**
     * Safely extract Double parameter
     */
    protected Double getDouble(Map<String, Object> args, String key, Double defaultValue) {
        if (args == null) return defaultValue;
        Object value = args.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return defaultValue;
    }

    /**
     * Safely extract Integer parameter
     */
    protected Integer getInteger(Map<String, Object> args, String key, Integer defaultValue) {
        if (args == null) return defaultValue;
        Object value = args.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }

    // ========== Schema Builder Helpers ==========

    /**
     * Build a simple string parameter schema
     */
    protected Schema stringParam(String description) {
        return Schema.builder()
            .type("string")
            .description(description)
            .build();
    }

    /**
     * Build a number parameter schema
     */
    protected Schema numberParam(String description) {
        return Schema.builder()
            .type("number")
            .description(description)
            .build();
    }

    /**
     * Build an integer parameter schema
     */
    protected Schema integerParam(String description) {
        return Schema.builder()
            .type("integer")
            .description(description)
            .build();
    }
}
