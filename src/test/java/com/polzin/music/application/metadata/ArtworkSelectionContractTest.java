package com.polzin.music.application.metadata;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArtworkSelectionContractTest {

    @Test
    void prefersLocalArtworkWhenAvailable() {
        ArtworkSelectionService service = new ArtworkSelectionService();

        var chosen = service.chooseArtwork(List.of(
                new ArtworkSelectionService.ArtworkCandidate("COVER_ART_ARCHIVE", "remote-ref", "image/jpeg", false),
                new ArtworkSelectionService.ArtworkCandidate("LOCAL_FOLDER_IMAGE", "local-ref", "image/png", true)
        ));

        assertTrue(chosen.isPresent());
        assertEquals("local-ref", chosen.orElseThrow().reference());
    }
}
