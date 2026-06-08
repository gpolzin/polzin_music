package com.polzin.music.application.importer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DuplicateDetectorTest {

    private final DuplicateDetectionService service = new DuplicateDetectionService();

    @Test
    void groupsCandidatesByNormalizedTitleAndArtist() {
        List<DuplicateDetectionService.DuplicateCandidate> candidates = List.of(
                new DuplicateDetectionService.DuplicateCandidate("Musica", "Artista", 1000, 128, 44100, 180, "mp3"),
                new DuplicateDetectionService.DuplicateCandidate("musica", "artista", 1200, 192, 44100, 180, "mp3"),
                new DuplicateDetectionService.DuplicateCandidate("Outra", "Artista", 900, 128, 44100, 200, "mp3")
        );

        List<DuplicateDetectionService.DuplicateGroup> groups = service.groupCandidates(candidates);

        assertEquals(1, groups.size());
        assertEquals(2, groups.getFirst().candidates().size());
    }

    @Test
    void choosesBestCandidateUsingTechnicalQualitySignals() {
        List<DuplicateDetectionService.DuplicateCandidate> candidates = List.of(
                new DuplicateDetectionService.DuplicateCandidate("Musica", "Artista", 1000, 128, 44100, 180, "mp3"),
                new DuplicateDetectionService.DuplicateCandidate("Musica", "Artista", 1500, 320, 48000, 185, "flac")
        );

        DuplicateDetectionService.DuplicateCandidate best = service.chooseBestCandidate(candidates);

        assertFalse(best.codec().equalsIgnoreCase("mp3"));
        assertEquals(1500, best.fileSizeBytes());
    }
}
