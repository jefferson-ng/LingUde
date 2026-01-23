package com.sep.sep_backend.ai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Azure Speech pronunciation assessment.
 */
@Configuration
public class AzureSpeechConfig {

    @Value("${azure.speech.key:}")
    private String subscriptionKey;

    @Value("${azure.speech.region:}")
    private String region;

    @Value("${azure.speech.language:en-US}")
    private String defaultLanguage;

    public String getSubscriptionKey() {
        return subscriptionKey;
    }

    public String getRegion() {
        return region;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }
}
