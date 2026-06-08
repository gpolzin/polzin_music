package com.polzin.music.integration.player;

import com.polzin.music.application.player.ListeningHistoryService;
import com.polzin.music.infrastructure.db.DatabaseManager;
import com.polzin.music.infrastructure.db.repository.PlaybackRepository;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListeningHistoryIT {

    @Test
    void persistsLastPlayedEventForTrack() throws Exception {
        Path databaseFile = Files.createTempFile("polzin-listening-history", ".db");
        try (DatabaseManager databaseManager = new DatabaseManager("jdbc:sqlite:" + databaseFile.toAbsolutePath())) {
            databaseManager.initialize();
            PlaybackRepository repository = new PlaybackRepository(databaseManager);
            ListeningHistoryService service = new ListeningHistoryService(repository);

            UUID albumId = UUID.fromString("22222222-2222-2222-2222-222222222222");
            UUID trackId = UUID.fromString("33333333-3333-3333-3333-333333333333");

            try (Connection connection = databaseManager.openConnection()) {
            connection.createStatement().executeUpdate("""
                INSERT INTO artists (id, name)
                VALUES ('11111111-1111-1111-1111-111111111111', 'Artista')
                """);
            connection.createStatement().executeUpdate("""
                INSERT INTO albums (id, artist_id, title, album_type)
                VALUES ('22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', 'Album', 'STUDIO')
                """);
            connection.createStatement().executeUpdate("""
                INSERT INTO tracks (id, album_id, artist_id, title, track_number, duration_seconds, source_file_path, extension, metadata_source)
                VALUES ('33333333-3333-3333-3333-333333333333', '22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', 'Faixa', 1, 240, 'track.mp3', 'mp3', 'ID3V2')
                """);
            }

            service.record(new ListeningHistoryService.ListeningSession(
                    trackId,
                    albumId,
                    Instant.parse("2026-06-08T17:00:00Z"),
                    Instant.parse("2026-06-08T17:04:00Z"),
                    1.0,
                    "PLAYER_UI"
            ));

            assertEquals(albumId, repository.findLatestByTrackId(trackId).orElseThrow().albumId());
            try (Connection connection = databaseManager.openConnection();
                 var trackResult = connection.createStatement().executeQuery("SELECT last_listened_at FROM tracks WHERE id = '33333333-3333-3333-3333-333333333333'" );
                 var albumResult = connection.createStatement().executeQuery("SELECT last_listened_at FROM albums WHERE id = '22222222-2222-2222-2222-222222222222'")) {
                trackResult.next();
                albumResult.next();
                assertEquals("2026-06-08T17:04:00Z", trackResult.getString(1));
                assertEquals("2026-06-08T17:04:00Z", albumResult.getString(1));
            }
        } finally {
            Files.deleteIfExists(databaseFile);
        }
    }
}
