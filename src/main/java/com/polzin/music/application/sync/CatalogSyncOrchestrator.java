package com.polzin.music.application.sync;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class CatalogSyncOrchestrator {

    public record CatalogEntry(String provider,
                               String externalId,
                               String canonicalReference,
                               String title,
                               String albumType,
                               Integer releaseYear,
                               double confidence) {
    }

    public record ProviderReference(String provider, String reference) {
    }

    public record MissingAlbum(String canonicalReference,
                               List<ProviderReference> providerReferences,
                               String title,
                               String albumType,
                               Integer releaseYear,
                               double confidence,
                               List<String> sourcePriorityUsed,
                               boolean needsUserReview) {
    }

    public record CatalogSyncResult(String status,
                                    double coverageRatio,
                                    List<MissingAlbum> missingAlbums,
                                    List<String> provenanceSummary) {
    }

    public CatalogSyncResult reconcile(String artistId,
                                       List<CatalogEntry> remoteEntries,
                                       List<String> localOwnedAlbumTitles) {
        Objects.requireNonNull(artistId, "artistId");
        List<CatalogEntry> entries = remoteEntries == null ? List.of() : remoteEntries;
        List<String> localTitles = localOwnedAlbumTitles == null ? List.of() : localOwnedAlbumTitles;

        Map<String, List<CatalogEntry>> groupedByCanonicalReference = new LinkedHashMap<>();
        for (CatalogEntry entry : entries) {
            groupedByCanonicalReference.computeIfAbsent(resolveCanonicalReference(entry), key -> new ArrayList<>()).add(entry);
        }

        List<MissingAlbum> missingAlbums = new ArrayList<>();
        List<String> provenanceSummary = new ArrayList<>();
        int matchedCount = 0;

        for (Map.Entry<String, List<CatalogEntry>> groupedEntry : groupedByCanonicalReference.entrySet()) {
            CatalogEntry canonicalEntry = chooseCanonical(groupedEntry.getValue());
            provenanceSummary.add(canonicalEntry.provider() + ":" + canonicalEntry.externalId());

            boolean ownedLocally = localTitles.stream().anyMatch(title -> title.equalsIgnoreCase(canonicalEntry.title()));
            if (ownedLocally) {
                matchedCount++;
                continue;
            }

            List<ProviderReference> providerReferences = groupedEntry.getValue().stream()
                    .map(entry -> new ProviderReference(entry.provider(), entry.externalId()))
                    .toList();
            missingAlbums.add(new MissingAlbum(
                    canonicalEntry.canonicalReference(),
                    providerReferences,
                    canonicalEntry.title(),
                    canonicalEntry.albumType(),
                    canonicalEntry.releaseYear(),
                    highestConfidence(groupedEntry.getValue()),
                    groupedEntry.getValue().stream().map(CatalogEntry::provider).distinct().toList(),
                    highestConfidence(groupedEntry.getValue()) < 0.75
            ));
        }

        double coverageRatio = entries.isEmpty() ? 1.0 : (double) matchedCount / groupedByCanonicalReference.size();
        String status = missingAlbums.isEmpty() ? "OK" : "OK";
        return new CatalogSyncResult(status, coverageRatio, List.copyOf(missingAlbums), List.copyOf(provenanceSummary));
    }

    private CatalogEntry chooseCanonical(List<CatalogEntry> entries) {
        return entries.stream()
                .filter(entry -> "MUSICBRAINZ".equalsIgnoreCase(entry.provider()))
                .findFirst()
                .orElse(entries.getFirst());
    }

    private String resolveCanonicalReference(CatalogEntry entry) {
        if (entry.canonicalReference() != null && !entry.canonicalReference().isBlank()) {
            return entry.canonicalReference();
        }
        return entry.provider() + ":" + entry.externalId();
    }

    private double highestConfidence(List<CatalogEntry> entries) {
        return entries.stream()
                .mapToDouble(CatalogEntry::confidence)
                .max()
                .orElse(0.0);
    }
}
