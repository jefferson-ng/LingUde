package com.sep.sep_backend.ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep.sep_backend.ai.config.AzureSpeechConfig;
import com.sep.sep_backend.ai.dto.PronunciationAnalyzeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * Service for Azure Speech pronunciation assessment.
 */
@Service
public class AzureSpeechService {

    private static final Logger log = LoggerFactory.getLogger(AzureSpeechService.class);
    private static final String DEFAULT_CONTENT_TYPE = "audio/wav; codecs=audio/pcm; samplerate=16000";

    private final AzureSpeechConfig config;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public AzureSpeechService(AzureSpeechConfig config, ObjectMapper objectMapper) {
        this.config = config;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newHttpClient();
    }

    public PronunciationAnalyzeResponse analyze(byte[] audioBytes, String referenceText, String languageOverride) {
        PronunciationAnalyzeResponse response = new PronunciationAnalyzeResponse();

        if (config.getSubscriptionKey() == null || config.getSubscriptionKey().isBlank()) {
            response.setSuccess(false);
            response.setError("Azure Speech key is not configured.");
            return response;
        }

        if (config.getRegion() == null || config.getRegion().isBlank()) {
            response.setSuccess(false);
            response.setError("Azure Speech region is not configured.");
            return response;
        }

        String language = languageOverride != null && !languageOverride.isBlank()
            ? languageOverride
            : config.getDefaultLanguage();

        try {
            String assessmentParams = objectMapper.writeValueAsString(Map.of(
                "ReferenceText", referenceText,
                "GradingSystem", "HundredMark",
                "Granularity", "Word",
                "EnableMiscue", true
            ));
            String assessmentHeader = Base64.getEncoder()
                .encodeToString(assessmentParams.getBytes(StandardCharsets.UTF_8));

            String endpoint = String.format(
                "https://%s.stt.speech.microsoft.com/speech/recognition/conversation/cognitiveservices/v1" +
                    "?language=%s&format=detailed&profanity=masked",
                config.getRegion(),
                URLEncoder.encode(language, StandardCharsets.UTF_8)
            );

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Ocp-Apim-Subscription-Key", config.getSubscriptionKey())
                .header("Pronunciation-Assessment", assessmentHeader)
                .header("Content-Type", DEFAULT_CONTENT_TYPE)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofByteArray(audioBytes))
                .build();

            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (httpResponse.statusCode() < 200 || httpResponse.statusCode() >= 300) {
                log.warn("Azure Speech returned status {}: {}", httpResponse.statusCode(), httpResponse.body());
                response.setSuccess(false);
                response.setError("Azure Speech request failed with status " + httpResponse.statusCode());
                return response;
            }

            log.info("Azure Speech API Response: {}", httpResponse.body());
            return parseAssessment(httpResponse.body());
        } catch (Exception e) {
            log.error("Azure Speech pronunciation assessment failed: {}", e.getMessage(), e);
            response.setSuccess(false);
            response.setError("Azure Speech pronunciation assessment failed.");
            return response;
        }
    }

    private PronunciationAnalyzeResponse parseAssessment(String jsonBody) throws JsonProcessingException {
        PronunciationAnalyzeResponse response = new PronunciationAnalyzeResponse();
        Map<String, Object> payload = objectMapper.readValue(jsonBody, Map.class);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nBest = (List<Map<String, Object>>) payload.get("NBest");
        if (nBest == null || nBest.isEmpty()) {
            response.setSuccess(false);
            response.setError("No assessment results returned.");
            return response;
        }

        Map<String, Object> best = nBest.get(0);
        response.setTranscript((String) best.get("Display"));

        // Try to get PronunciationAssessment nested object first (detailed format)
        @SuppressWarnings("unchecked")
        Map<String, Object> pronunciation = (Map<String, Object>) best.get("PronunciationAssessment");

        if (pronunciation != null) {
            // Detailed pronunciation assessment format
            response.setAccuracyScore(getDouble(pronunciation, "AccuracyScore"));
            response.setFluencyScore(getDouble(pronunciation, "FluencyScore"));
            response.setCompletenessScore(getDouble(pronunciation, "CompletenessScore"));
            response.setPronunciationScore(getDouble(pronunciation, "PronScore"));
        } else {
            // Simple format - scores are directly in NBest[0]
            response.setAccuracyScore(getDouble(best, "AccuracyScore"));
            response.setFluencyScore(getDouble(best, "FluencyScore"));
            response.setCompletenessScore(getDouble(best, "CompletenessScore"));
            response.setPronunciationScore(getDouble(best, "PronScore"));

            // If PronScore is not present, use AccuracyScore as fallback
            if (response.getPronunciationScore() == null && response.getAccuracyScore() != null) {
                response.setPronunciationScore(response.getAccuracyScore());
            }
        }

        log.info("Parsed overall scores - Accuracy: {}, Fluency: {}, Completeness: {}, Pronunciation: {}",
            response.getAccuracyScore(), response.getFluencyScore(),
            response.getCompletenessScore(), response.getPronunciationScore());

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> words = (List<Map<String, Object>>) best.get("Words");
        if (words != null) {
            for (Map<String, Object> wordEntry : words) {
                PronunciationAnalyzeResponse.WordScore wordScore = new PronunciationAnalyzeResponse.WordScore();
                wordScore.setWord((String) wordEntry.get("Word"));
                wordScore.setOffset(getInteger(wordEntry, "Offset"));
                wordScore.setDuration(getInteger(wordEntry, "Duration"));

                // Try to get PronunciationAssessment nested object first (detailed format)
                @SuppressWarnings("unchecked")
                Map<String, Object> wordAssessment = (Map<String, Object>) wordEntry.get("PronunciationAssessment");

                if (wordAssessment != null) {
                    // Detailed pronunciation assessment format
                    wordScore.setAccuracyScore(getDouble(wordAssessment, "AccuracyScore"));
                    wordScore.setErrorType((String) wordAssessment.get("ErrorType"));
                } else {
                    // Simple format - scores are directly in the word object
                    wordScore.setAccuracyScore(getDouble(wordEntry, "AccuracyScore"));
                    wordScore.setErrorType((String) wordEntry.get("ErrorType"));
                }

                log.info("Word '{}' - Score: {}, ErrorType: {}",
                    wordScore.getWord(), wordScore.getAccuracyScore(), wordScore.getErrorType());

                response.getWords().add(wordScore);
            }
        }

        response.setSuccess(true);
        log.info("Successfully parsed {} words with scores", response.getWords().size());
        return response;
    }

    private Double getDouble(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return null;
    }

    private Integer getInteger(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return null;
    }
}
