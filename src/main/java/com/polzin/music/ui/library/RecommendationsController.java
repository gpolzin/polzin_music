package com.polzin.music.ui.library;

import com.polzin.music.application.recommendation.RecommendationService;

import java.util.List;

public final class RecommendationsController {

    private final RecommendationService recommendationService;

    public RecommendationsController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    public List<RecommendationService.RecommendationItem> recommend(List<RecommendationService.RecommendationCandidate> candidates) {
        return recommendationService.rankCandidates(candidates);
    }
}
