package com.polzin.music.contract.sync;

import com.polzin.music.application.sync.CatalogSyncOrchestrator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MultiSourceReconciliationContractTest {

    @Test
    void prefersCanonicalMusicBrainzReferenceAndPreservesProviderProvenance() {
        CatalogSyncOrchestrator orchestrator = new CatalogSyncOrchestrator();

        CatalogSyncOrchestrator.CatalogSyncResult result = orchestrator.reconcile(
                "artist-1",
                List.of(
                        new CatalogSyncOrchestrator.CatalogEntry("MUSICBRAINZ", "mb-1", "MB:release-group-1", "Album Alpha", "STUDIO", 2024, 0.95),
                        new CatalogSyncOrchestrator.CatalogEntry("DISCOGS", "disc-99", "MB:release-group-1", "Album Alpha", "STUDIO", 2024, 0.80)
                ),
                List.of()
        );

        assertEquals("OK", result.status());
        assertFalse(result.missingAlbums().isEmpty());
        assertEquals("MB:release-group-1", result.missingAlbums().getFirst().canonicalReference());
        assertTrue(result.missingAlbums().getFirst().providerReferences().stream().anyMatch(reference -> reference.provider().equals("DISCOGS")));
        assertTrue(result.missingAlbums().getFirst().providerReferences().stream().anyMatch(reference -> reference.provider().equals("MUSICBRAINZ")));
        assertTrue(result.provenanceSummary().contains("MUSICBRAINZ:mb-1"));
    }
}
