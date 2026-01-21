package com.sep.sep_backend.ai.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Result object from ConversationService that includes
 * the AI response and any tool calls that were executed.
 */
public class ConversationResult {

    private String response;
    private List<ToolCallInfo> toolCalls;

    public ConversationResult() {
        this.toolCalls = new ArrayList<>();
    }

    public ConversationResult(String response) {
        this.response = response;
        this.toolCalls = new ArrayList<>();
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public List<ToolCallInfo> getToolCalls() {
        return toolCalls;
    }

    public void setToolCalls(List<ToolCallInfo> toolCalls) {
        this.toolCalls = toolCalls;
    }

    public void addToolCall(ToolCallInfo toolCall) {
        if (this.toolCalls == null) {
            this.toolCalls = new ArrayList<>();
        }
        this.toolCalls.add(toolCall);
    }

    /**
     * Information about a single tool call execution
     */
    public static class ToolCallInfo {
        private String name;
        private Map<String, Object> arguments;
        private Object result;
        private long durationMs;

        public ToolCallInfo() {
        }

        public ToolCallInfo(String name, Map<String, Object> arguments, Object result, long durationMs) {
            this.name = name;
            this.arguments = arguments;
            this.result = result;
            this.durationMs = durationMs;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Map<String, Object> getArguments() {
            return arguments;
        }

        public void setArguments(Map<String, Object> arguments) {
            this.arguments = arguments;
        }

        public Object getResult() {
            return result;
        }

        public void setResult(Object result) {
            this.result = result;
        }

        public long getDurationMs() {
            return durationMs;
        }

        public void setDurationMs(long durationMs) {
            this.durationMs = durationMs;
        }
    }
}
