package com.sep.sep_backend.ai.config;

import com.google.genai.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Google Gemini AI integration.
 * Creates and configures the Gemini client bean with settings from application.properties.
 */
@Configuration
public class GeminiConfig {

    @Value("${google.gemini.api-key}")
    private String apiKey;

    @Value("${google.gemini.model}")
    private String model;

    @Value("${google.gemini.temperature:0.7}")
    private Double temperature;

    @Value("${google.gemini.max-tokens:2048}")
    private Integer maxTokens;

    /**
     * Creates the Gemini AI client bean.
     * The client is configured with the API key from application.properties.
     *
     * @return Configured Gemini client instance
     */
    @Bean
    public Client geminiClient() {
        return Client.builder()
                .apiKey(apiKey)
                .build();
    }

    // Getters for configuration values (used by other components)

    public String getModel() {
        return model;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }
}
