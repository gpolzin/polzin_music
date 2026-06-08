package com.polzin.music.application.organizer;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LibraryOrganizerServiceTest {

    @Test
    void buildsDeterministicAlbumAndTrackPaths() {
        LibraryOrganizerService service = new LibraryOrganizerService();
        Path albumDirectory = service.buildAlbumDirectory(
                Path.of("C:/organized"),
                "Artista Principal",
                LibraryOrganizerService.AlbumType.LIVE,
                2026,
                "Show Final"
        );

        Path trackPath = service.buildTrackPath(albumDirectory, 3, "Música ao vivo", "MP3");

        assertEquals("C:/organized/Artistas/Artista Principal/Ao Vivo/2026 - Show Final", albumDirectory.toString().replace('\\', '/'));
        assertEquals("C:/organized/Artistas/Artista Principal/Ao Vivo/2026 - Show Final/03 - Música ao vivo.mp3", trackPath.toString().replace('\\', '/'));
    }
}
