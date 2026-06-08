package com.polzin.music.infrastructure.db.repository;

import com.polzin.music.infrastructure.db.DatabaseManager;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DuplicateResolutionRepositoryTest {

    @Test
    void savesAndLoadsDuplicateResolution() throws Exception {
        Path databaseFile = Files.createTempFile("polzin-resolution", ".db");
        try (DatabaseManager databaseManager = new DatabaseManager("jdbc:sqlite:" + databaseFile.toAbsolutePath())) {
            databaseManager.initialize();
            DuplicateResolutionRepository repository = new DuplicateResolutionRepository(databaseManager);

            try (Connection connection = databaseManager.openConnection()) {
            connection.createStatement().executeUpdate("""
                INSERT INTO import_jobs (id, source_root, destination_root, status)
                VALUES ('job-1', 'source', 'dest', 'RUNNING')
                """);
            connection.createStatement().executeUpdate("""
                INSERT INTO duplicate_groups (id, import_job_id, status)
                VALUES ('group-1', 'job-1', 'OPEN')
                """);
            }

            repository.save(new DuplicateResolutionRepository.DuplicateResolutionRecord(
                    "group-1",
                    "candidate-2",
                    List.of("candidate-1"),
                    "USER",
                    "prefer higher bitrate",
                    Instant.parse("2026-06-08T17:00:00Z")
            ));

            assertTrue(repository.findByGroupId("group-1").isPresent());
            assertEquals("candidate-2", repository.findByGroupId("group-1").orElseThrow().selectedCandidateId());
        } finally {
            Files.deleteIfExists(databaseFile);
        }
    }
}
