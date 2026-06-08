package com.polzin.music.integration.sync;

import com.polzin.music.application.sync.CatalogSyncOrchestrator;
import com.polzin.music.application.sync.MissingAlbumService;
import com.polzin.music.infrastructure.db.repository.CatalogSyncRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class MissingAlbumNotificationIT {

    @Test
    void persistsMissingAlbumNotificationDetails() {
        CatalogSyncRepository repository = new CatalogSyncRepository();
        MissingAlbumService service = new MissingAlbumService(new CatalogSyncOrchestrator(), repository);

        UUID artistId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        service.detectAndPersist(new MissingAlbumService.MissingAlbumCheck(
                artistId,
                List.of(
                        new CatalogSyncOrchestrator.CatalogEntry("MUSICBRAINZ", "mb-3", "MB:rg-3", "Album C", "EP", 2022, 0.88)
                ),
                List.of()
        ));

        assertEquals(1, repository.findByArtistId(artistId).size());
        assertFalse(repository.findByArtistId(artistId).getFirst().providerReferences().isEmpty());
    }
}
