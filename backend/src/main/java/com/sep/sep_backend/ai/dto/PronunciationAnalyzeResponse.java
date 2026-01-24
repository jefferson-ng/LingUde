package com.sep.sep_backend.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Response DTO for pronunciation assessment results.
 */
public class PronunciationAnalyzeResponse {

    private boolean success;
    private String transcript;

    @JsonProperty("accuracyScore")
    private Double accuracyScore;

    @JsonProperty("fluencyScore")
    private Double fluencyScore;

    @JsonProperty("completenessScore")
    private Double completenessScore;

    @JsonProperty("pronunciationScore")
    private Double pronunciationScore;

    private List<WordScore> words = new ArrayList<>();
    private String error;

    @JsonProperty("xpAwarded")
    private Integer xpAwarded;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    public Double getAccuracyScore() {
        return accuracyScore;
    }

    public void setAccuracyScore(Double accuracyScore) {
        this.accuracyScore = accuracyScore;
    }

    public Double getFluencyScore() {
        return fluencyScore;
    }

    public void setFluencyScore(Double fluencyScore) {
        this.fluencyScore = fluencyScore;
    }

    public Double getCompletenessScore() {
        return completenessScore;
    }

    public void setCompletenessScore(Double completenessScore) {
        this.completenessScore = completenessScore;
    }

    public Double getPronunciationScore() {
        return pronunciationScore;
    }

    public void setPronunciationScore(Double pronunciationScore) {
        this.pronunciationScore = pronunciationScore;
    }

    public List<WordScore> getWords() {
        return words;
    }

    public void setWords(List<WordScore> words) {
        this.words = words;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Integer getXpAwarded() {
        return xpAwarded;
    }

    public void setXpAwarded(Integer xpAwarded) {
        this.xpAwarded = xpAwarded;
    }

    public static class WordScore {
        private String word;

        @JsonProperty("accuracyScore")
        private Double accuracyScore;

        @JsonProperty("errorType")
        private String errorType;

        private Integer offset;
        private Integer duration;

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public Double getAccuracyScore() {
            return accuracyScore;
        }

        public void setAccuracyScore(Double accuracyScore) {
            this.accuracyScore = accuracyScore;
        }

        public String getErrorType() {
            return errorType;
        }

        public void setErrorType(String errorType) {
            this.errorType = errorType;
        }

        public Integer getOffset() {
            return offset;
        }

        public void setOffset(Integer offset) {
            this.offset = offset;
        }

        public Integer getDuration() {
            return duration;
        }

        public void setDuration(Integer duration) {
            this.duration = duration;
        }
    }
}
