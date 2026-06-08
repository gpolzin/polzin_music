package com.polzin.music.ui.library;

import com.polzin.music.application.recommendation.RecommendationService;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecommendationsControllerTest {

    @Test
    void delegatesToRecommendationService() {
        RecommendationsController controller = new RecommendationsController(
                new RecommendationService(Clock.fixed(Instant.parse("2026-06-08T17:00:00Z"), ZoneOffset.UTC), RecommendationService.RecommendationWeights.defaults())
        );

        List<RecommendationService.RecommendationItem> items = controller.recommend(List.of(
                new RecommendationService.RecommendationCandidate(UUID.randomUUID(), 5, Instant.parse("2026-05-01T00:00:00Z"), 0, 1.0, List.of("MUSICBRAINZ"))
        ));

        assertEquals(1, items.size());
    }
}
