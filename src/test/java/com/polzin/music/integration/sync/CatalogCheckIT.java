package com.polzin.music.integration.sync;

import com.polzin.music.application.sync.CatalogSyncOrchestrator;
import com.polzin.music.application.sync.MissingAlbumService;
import com.polzin.music.infrastructure.db.repository.CatalogSyncRepository;
import com.polzin.music.infrastructure.scheduler.CatalogCheckScheduler;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CatalogCheckIT {

    @Test
    void runsPeriodicCatalogChecksAndPersistsNotifications() {
        CatalogSyncOrchestrator orchestrator = new CatalogSyncOrchestrator();
        CatalogSyncRepository repository = new CatalogSyncRepository();
        MissingAlbumService missingAlbumService = new MissingAlbumService(orchestrator, repository);
        CatalogCheckScheduler scheduler = new CatalogCheckScheduler(missingAlbumService);

        UUID artistId = UUID.fromString("99999999-9999-9999-9999-999999999999");
        scheduler.runChecks(List.of(new CatalogCheckScheduler.CatalogCheckTask(
                new MissingAlbumService.MissingAlbumCheck(
                        artistId,
                        List.of(
                                new CatalogSyncOrchestrator.CatalogEntry("MUSICBRAINZ", "mb-2", "MB:rg-2", "Album B", "STUDIO", 2023, 0.90)
                        ),
                        List.of()
                )
        )));

        assertEquals(1, repository.findByArtistId(artistId).size());
    }
}
