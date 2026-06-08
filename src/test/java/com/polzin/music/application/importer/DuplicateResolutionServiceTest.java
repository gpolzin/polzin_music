package com.polzin.music.application.importer;

import com.polzin.music.infrastructure.db.DatabaseManager;
import com.polzin.music.infrastructure.db.repository.DuplicateResolutionRepository;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DuplicateResolutionServiceTest {

    @Test
    void persistsResolutionDecisionThroughRepository() throws Exception {
        Path databaseFile = Files.createTempFile("polzin-resolution-service", ".db");
        try (DatabaseManager databaseManager = new DatabaseManager("jdbc:sqlite:" + databaseFile.toAbsolutePath())) {
            databaseManager.initialize();
            DuplicateResolutionRepository repository = new DuplicateResolutionRepository(databaseManager);
            DuplicateResolutionService service = new DuplicateResolutionService(repository);

            try (Connection connection = databaseManager.openConnection()) {
            connection.createStatement().executeUpdate("""
                INSERT INTO import_jobs (id, source_root, destination_root, status)
                VALUES ('job-2', 'source', 'dest', 'RUNNING')
                """);
            connection.createStatement().executeUpdate("""
                INSERT INTO duplicate_groups (id, import_job_id, status)
                VALUES ('group-2', 'job-2', 'OPEN')
                """);
            }

            service.resolve(new DuplicateResolutionService.ResolutionDecision(
                    "group-2",
                    "candidate-b",
                    List.of("candidate-a"),
                    "USER",
                    "manual preference",
                    Instant.parse("2026-06-08T17:01:00Z")
            ));

            assertEquals("candidate-b", repository.findByGroupId("group-2").orElseThrow().selectedCandidateId());
        } finally {
            Files.deleteIfExists(databaseFile);
        }
    }
}
