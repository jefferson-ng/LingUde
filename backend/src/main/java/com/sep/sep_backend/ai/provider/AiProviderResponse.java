package com.sep.sep_backend.ai.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Standardized response format from AI providers.
 * Encapsulates both text responses and tool/function calls.
 */
public class AiProviderResponse {

    private String textContent;
    private List<ToolCall> toolCalls;
    private boolean hasFunctionCalls;

    public AiProviderResponse() {
        this.toolCalls = new ArrayList<>();
        this.hasFunctionCalls = false;
    }

    public AiProviderResponse(String textContent) {
        this();
        this.textContent = textContent;
    }

    public AiProviderResponse(String textContent, List<ToolCall> toolCalls, boolean hasFunctionCalls) {
        this.textContent = textContent;
        this.toolCalls = toolCalls != null ? toolCalls : new ArrayList<>();
        this.hasFunctionCalls = hasFunctionCalls;
    }

    /**
     * Represents a tool/function call requested by the AI
     */
    public static class ToolCall {
        private String functionName;
        private Map<String, Object> arguments;

        public ToolCall() {
        }

        public ToolCall(String functionName, Map<String, Object> arguments) {
            this.functionName = functionName;
            this.arguments = arguments;
        }

        public String getFunctionName() {
            return functionName;
        }

        public void setFunctionName(String functionName) {
            this.functionName = functionName;
        }

        public Map<String, Object> getArguments() {
            return arguments;
        }

        public void setArguments(Map<String, Object> arguments) {
            this.arguments = arguments;
        }
    }

    // Getters and Setters
    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public List<ToolCall> getToolCalls() {
        return toolCalls;
    }

    public void setToolCalls(List<ToolCall> toolCalls) {
        this.toolCalls = toolCalls;
    }

    public boolean isHasFunctionCalls() {
        return hasFunctionCalls;
    }

    public boolean isHasToolCalls() {
        return hasFunctionCalls;
    }

    public void setHasFunctionCalls(boolean hasFunctionCalls) {
        this.hasFunctionCalls = hasFunctionCalls;
    }
}
