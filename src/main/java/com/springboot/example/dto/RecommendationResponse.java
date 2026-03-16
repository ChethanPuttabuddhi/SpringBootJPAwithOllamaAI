package com.springboot.example.dto;

import java.util.List;

public class RecommendationResponse {

    private List<String> recommendations;
    private String raw;

    public RecommendationResponse() {}

    public RecommendationResponse(List<String> recommendations, String raw) {
        this.recommendations = recommendations;
        this.raw = raw;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }
}