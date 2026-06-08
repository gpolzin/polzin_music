package com.polzin.music.application.recommendation;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecommendationScoreTest {

    @Test
    void ranksLongUnheardHighRatedAlbumAboveRecentlyPlayedAlbum() {
        Clock clock = Clock.fixed(Instant.parse("2026-06-08T17:00:00Z"), ZoneOffset.UTC);
        RecommendationService service = new RecommendationService(clock, RecommendationService.RecommendationWeights.defaults());

        List<RecommendationService.RecommendationCandidate> candidates = List.of(
                new RecommendationService.RecommendationCandidate(
                        UUID.fromString("44444444-4444-4444-4444-444444444444"),
                        5,
                        Instant.parse("2026-05-01T00:00:00Z"),
                        1,
                        0.95,
                        List.of("MUSICBRAINZ", "COVER_ART_ARCHIVE")
                ),
                new RecommendationService.RecommendationCandidate(
                        UUID.fromString("55555555-5555-5555-5555-555555555555"),
                        2,
                        Instant.parse("2026-06-07T00:00:00Z"),
                        9,
                        0.60,
                        List.of("MUSICBRAINZ")
                )
        );

        List<RecommendationService.RecommendationItem> ranked = service.rankCandidates(candidates);

        assertEquals(UUID.fromString("44444444-4444-4444-4444-444444444444"), ranked.getFirst().albumId());
        assertTrue(ranked.getFirst().reasonCodes().contains("HIGH_RATING"));
        assertTrue(ranked.getFirst().reasonCodes().contains("LONG_TIME_UNHEARD"));
    }

    @Test
    void scoreReflectsRatingRecencyAndRepeatPenalty() {
        Clock clock = Clock.fixed(Instant.parse("2026-06-08T17:00:00Z"), ZoneOffset.UTC);
        RecommendationService service = new RecommendationService(clock, RecommendationService.RecommendationWeights.defaults());

        RecommendationService.RecommendationItem item = service.scoreCandidate(
                new RecommendationService.RecommendationCandidate(
                        UUID.fromString("66666666-6666-6666-6666-666666666666"),
                        5,
                        Instant.parse("2026-05-01T00:00:00Z"),
                        0,
                        1.0,
                        List.of("MUSICBRAINZ")
                )
        );

        assertTrue(item.score() > 0.6);
        assertEquals(1.0, item.metadataConfidence());
    }
}
