package com.polzin.music.application.importer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class DuplicateDetectionService {

    public record DuplicateCandidate(String title,
                                    String artist,
                                    long fileSizeBytes,
                                    Integer bitrateKbps,
                                    Integer sampleRateHz,
                                    Integer durationSeconds,
                                    String codec) {
    }

    public record DuplicateGroup(String normalizedTitle, String normalizedArtist, List<DuplicateCandidate> candidates) {
    }

    public List<DuplicateGroup> groupCandidates(List<DuplicateCandidate> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return List.of();
        }
        return candidates.stream()
                .collect(Collectors.groupingBy(candidate -> normalize(candidate.title()) + "|" + normalize(candidate.artist())))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() > 1)
                .map(entry -> {
                    List<DuplicateCandidate> grouped = new ArrayList<>(entry.getValue());
                    grouped.sort(candidateComparator());
                    String[] keyParts = entry.getKey().split("\\|", 2);
                    return new DuplicateGroup(keyParts[0], keyParts[1], grouped);
                })
                .sorted(Comparator.comparing(DuplicateGroup::normalizedArtist).thenComparing(DuplicateGroup::normalizedTitle))
                .toList();
    }

    public DuplicateCandidate chooseBestCandidate(List<DuplicateCandidate> candidates) {
        return candidates.stream()
                .filter(Objects::nonNull)
                .sorted(candidateComparator())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("candidates must not be empty"));
    }

    private Comparator<DuplicateCandidate> candidateComparator() {
        return Comparator
                .comparingLong(DuplicateCandidate::fileSizeBytes).reversed()
                .thenComparing((DuplicateCandidate candidate) -> candidate.bitrateKbps() == null ? 0 : candidate.bitrateKbps(), Comparator.reverseOrder())
                .thenComparing((DuplicateCandidate candidate) -> candidate.sampleRateHz() == null ? 0 : candidate.sampleRateHz(), Comparator.reverseOrder())
                .thenComparing((DuplicateCandidate candidate) -> candidate.durationSeconds() == null ? 0 : candidate.durationSeconds(), Comparator.reverseOrder())
                .thenComparing(candidate -> normalize(candidate.codec()), Comparator.reverseOrder());
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
