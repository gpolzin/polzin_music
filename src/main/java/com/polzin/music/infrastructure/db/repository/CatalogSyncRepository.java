package com.polzin.music.infrastructure.db.repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CatalogSyncRepository {

    public record MissingAlbumNotificationRecord(UUID artistId,
                                                 String albumTitle,
                                                 Integer releaseYear,
                                                 String providerReference,
                                                 Instant detectedAt,
                                                 Instant dismissedAt,
                                                 Instant acquiredAt,
                                                 double confidence,
                                                 List<String> sourcePriorityUsed,
                                                 List<String> providerReferences) {
    }

    private final Map<UUID, List<MissingAlbumNotificationRecord>> recordsByArtist = new ConcurrentHashMap<>();

    public void save(MissingAlbumNotificationRecord record) {
        recordsByArtist.computeIfAbsent(record.artistId(), key -> new ArrayList<>()).add(record);
    }

    public List<MissingAlbumNotificationRecord> findByArtistId(UUID artistId) {
        return Collections.unmodifiableList(recordsByArtist.getOrDefault(artistId, List.of()));
    }
}
