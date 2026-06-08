package com.polzin.music.application.library;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class CurationService {

    public record AlbumCuration(UUID albumId, Integer userRating) {
    }

    public record TrackTagAssignment(UUID trackId, String tagName) {
    }

    private final Map<UUID, Integer> albumRatings = new LinkedHashMap<>();
    private final Map<UUID, Set<String>> trackTags = new LinkedHashMap<>();

    public AlbumCuration rateAlbum(UUID albumId, int rating) {
        validateRating(rating);
        albumRatings.put(Objects.requireNonNull(albumId, "albumId"), rating);
        return new AlbumCuration(albumId, rating);
    }

    public TrackTagAssignment addTag(UUID trackId, String tagName) {
        Objects.requireNonNull(trackId, "trackId");
        String normalizedTag = normalizeTag(tagName);
        trackTags.computeIfAbsent(trackId, key -> new java.util.LinkedHashSet<>()).add(normalizedTag);
        return new TrackTagAssignment(trackId, normalizedTag);
    }

    public TrackTagAssignment removeTag(UUID trackId, String tagName) {
        Objects.requireNonNull(trackId, "trackId");
        String normalizedTag = normalizeTag(tagName);
        Set<String> tags = trackTags.get(trackId);
        if (tags != null) {
            tags.remove(normalizedTag);
            if (tags.isEmpty()) {
                trackTags.remove(trackId);
            }
        }
        return new TrackTagAssignment(trackId, normalizedTag);
    }

    public Map<UUID, Integer> albumRatings() {
        return Map.copyOf(albumRatings);
    }

    public List<String> tagsForTrack(UUID trackId) {
        return new ArrayList<>(trackTags.getOrDefault(trackId, Set.of()));
    }

    public List<TrackTagAssignment> allTagAssignments() {
        return trackTags.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream().map(tag -> new TrackTagAssignment(entry.getKey(), tag)))
                .collect(Collectors.toUnmodifiableList());
    }

    private void validateRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("rating must be between 1 and 5");
        }
    }

    private String normalizeTag(String tagName) {
        if (tagName == null || tagName.isBlank()) {
            throw new IllegalArgumentException("tagName must not be blank");
        }
        return tagName.trim();
    }
}
