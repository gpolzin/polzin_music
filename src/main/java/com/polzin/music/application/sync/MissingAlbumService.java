package com.polzin.music.application.sync;

import com.polzin.music.infrastructure.db.repository.CatalogSyncRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class MissingAlbumService {

    public record MissingAlbumCheck(UUID artistId,
                                    List<CatalogSyncOrchestrator.CatalogEntry> remoteEntries,
                                    List<String> localOwnedAlbumTitles) {
    }

    private final CatalogSyncOrchestrator catalogSyncOrchestrator;
    private final CatalogSyncRepository catalogSyncRepository;

    public MissingAlbumService(CatalogSyncOrchestrator catalogSyncOrchestrator,
                               CatalogSyncRepository catalogSyncRepository) {
        this.catalogSyncOrchestrator = catalogSyncOrchestrator;
        this.catalogSyncRepository = catalogSyncRepository;
    }

    public CatalogSyncOrchestrator.CatalogSyncResult detectAndPersist(MissingAlbumCheck check) {
        CatalogSyncOrchestrator.CatalogSyncResult result = catalogSyncOrchestrator.reconcile(
                check.artistId().toString(),
                check.remoteEntries(),
                check.localOwnedAlbumTitles()
        );
        for (CatalogSyncOrchestrator.MissingAlbum missingAlbum : result.missingAlbums()) {
            catalogSyncRepository.save(new CatalogSyncRepository.MissingAlbumNotificationRecord(
                    check.artistId(),
                    missingAlbum.title(),
                    missingAlbum.releaseYear(),
                    missingAlbum.providerReferences().isEmpty() ? null : missingAlbum.providerReferences().getFirst().provider() + ":" + missingAlbum.providerReferences().getFirst().reference(),
                    Instant.now(),
                    null,
                    null,
                    missingAlbum.confidence(),
                    missingAlbum.sourcePriorityUsed(),
                    missingAlbum.providerReferences().stream().map(reference -> reference.provider() + ":" + reference.reference()).toList()
            ));
        }
        return result;
    }
}
