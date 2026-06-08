package com.polzin.music.application.metadata;

import java.util.List;
import java.util.Optional;

public final class ArtworkSelectionService {

    public record ArtworkCandidate(String source, String reference, String mimeType, boolean local) {
    }

    public Optional<ArtworkCandidate> chooseArtwork(List<ArtworkCandidate> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return Optional.empty();
        }
        return candidates.stream()
                .filter(candidate -> candidate.source() != null)
                .sorted((left, right) -> Boolean.compare(right.local(), left.local()))
                .findFirst();
    }
}
