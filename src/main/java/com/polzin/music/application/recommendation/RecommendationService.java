package com.polzin.music.application.recommendation;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class RecommendationService {

    public record RecommendationWeights(double ratingWeight,
                                        double recencyWeight,
                                        double repeatWeight) {

        public RecommendationWeights {
            if (ratingWeight < 0 || recencyWeight < 0 || repeatWeight < 0) {
                throw new IllegalArgumentException("weights must be non-negative");
            }
        }

        public static RecommendationWeights defaults() {
            return new RecommendationWeights(0.45, 0.35, 0.20);
        }
    }

    public record RecommendationCandidate(UUID albumId,
                                          Integer albumRating,
                                          Instant lastListenedAt,
                                          int recentPlayCountWindow30d,
                                          double metadataConfidence,
                                          List<String> metadataProviders) {
    }

    public record RecommendationItem(UUID albumId,
                                     double score,
                                     List<String> reasonCodes,
                                     double metadataConfidence,
                                     List<String> provenanceSummary) {
    }

    private final Clock clock;
    private final RecommendationWeights weights;

    public RecommendationService() {
        this(Clock.systemUTC(), RecommendationWeights.defaults());
    }

    public RecommendationService(Clock clock, RecommendationWeights weights) {
        this.clock = Objects.requireNonNull(clock, "clock");
        this.weights = Objects.requireNonNull(weights, "weights");
    }

    public List<RecommendationItem> rankCandidates(List<RecommendationCandidate> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return List.of();
        }
        return candidates.stream()
                .map(this::scoreCandidate)
                .sorted(Comparator.comparingDouble(RecommendationItem::score).reversed())
                .toList();
    }

    public RecommendationItem scoreCandidate(RecommendationCandidate candidate) {
        Objects.requireNonNull(candidate, "candidate");
        double normalizedRating = normalizeRating(candidate.albumRating());
        double normalizedDaysSinceLastListen = normalizeDaysSinceLastListen(candidate.lastListenedAt());
        double normalizedRepeatCount = normalizeRepeatCount(candidate.recentPlayCountWindow30d());

        double score = (weights.ratingWeight() * normalizedRating)
                + (weights.recencyWeight() * normalizedDaysSinceLastListen)
                - (weights.repeatWeight() * normalizedRepeatCount);

        return new RecommendationItem(
                candidate.albumId(),
                score,
                buildReasonCodes(normalizedRating, normalizedDaysSinceLastListen, normalizedRepeatCount),
                clamp(candidate.metadataConfidence()),
                candidate.metadataProviders() == null ? List.of() : List.copyOf(candidate.metadataProviders())
        );
    }

    private double normalizeRating(Integer albumRating) {
        if (albumRating == null) {
            return 0.0;
        }
        return clamp((albumRating - 1) / 4.0);
    }

    private double normalizeDaysSinceLastListen(Instant lastListenedAt) {
        if (lastListenedAt == null) {
            return 1.0;
        }
        long days = Duration.between(lastListenedAt, Instant.now(clock)).toDays();
        return clamp(days / 30.0);
    }

    private double normalizeRepeatCount(int repeatCount) {
        return clamp(repeatCount / 10.0);
    }

    private List<String> buildReasonCodes(double rating, double recency, double repeat) {
        java.util.ArrayList<String> reasonCodes = new java.util.ArrayList<>();
        if (rating >= 0.75) {
            reasonCodes.add("HIGH_RATING");
        }
        if (recency >= 0.75) {
            reasonCodes.add("LONG_TIME_UNHEARD");
        }
        if (repeat <= 0.25) {
            reasonCodes.add("LOW_REPEAT");
        }
        return List.copyOf(reasonCodes);
    }

    private double clamp(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return 0.0;
        }
        return Math.max(0.0, Math.min(1.0, value));
    }
}
