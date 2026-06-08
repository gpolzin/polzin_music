package com.polzin.music.integration.importer;

import com.polzin.music.application.importer.DuplicateResolutionService;
import com.polzin.music.application.importer.FileScannerService;
import com.polzin.music.application.importer.ImportJobPipeline;
import com.polzin.music.application.importer.MetadataExtractionService;
import com.polzin.music.application.importer.TagFallbackParser;
import com.polzin.music.application.jobs.JobCoordinator;
import com.polzin.music.infrastructure.db.DatabaseManager;
import com.polzin.music.infrastructure.db.repository.DuplicateResolutionRepository;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DuplicateReplayIT {

    @Test
    void replaysDuplicateDecisionWhenAGroupIsReimported() throws Exception {
        Path databaseFile = Files.createTempFile("polzin-duplicate-replay", ".db");
        try (DatabaseManager databaseManager = new DatabaseManager("jdbc:sqlite:" + databaseFile.toAbsolutePath())) {
            databaseManager.initialize();
            DuplicateResolutionRepository repository = new DuplicateResolutionRepository(databaseManager);
            DuplicateResolutionService service = new DuplicateResolutionService(repository);

            try (Connection connection = databaseManager.openConnection()) {
                connection.createStatement().executeUpdate("""
                        INSERT INTO import_jobs (id, source_root, destination_root, status)
                        VALUES ('job-replay', 'source', 'dest', 'RUNNING')
                        """);
                connection.createStatement().executeUpdate("""
                        INSERT INTO duplicate_groups (id, import_job_id, status)
                        VALUES ('group-replay', 'job-replay', 'OPEN')
                        """);
            }

            service.resolve(new DuplicateResolutionService.ResolutionDecision(
                    "group-replay",
                    "candidate-a",
                    List.of("candidate-b"),
                    "USER",
                    "first pass",
                    Instant.parse("2026-06-08T17:01:00Z")
            ));

            service.resolve(new DuplicateResolutionService.ResolutionDecision(
                    "group-replay",
                    "candidate-b",
                    List.of("candidate-a"),
                    "REIMPORT",
                    "replayed on re-import",
                    Instant.parse("2026-06-08T17:05:00Z")
            ));

            DuplicateResolutionRepository.DuplicateResolutionRecord record = repository.findByGroupId("group-replay").orElseThrow();
            assertEquals("candidate-b", record.selectedCandidateId());
            assertEquals(List.of("candidate-a"), record.discardedCandidateIds());
            assertEquals("REIMPORT", record.decidedBy());
        } finally {
            Files.deleteIfExists(databaseFile);
        }
    }

    @Test
    void resumesImportFromStoredCheckpointOnReImport() throws Exception {
        Path sourceRoot = Files.createTempDirectory("polzin-import-replay");
        try {
            Path albumDir = Files.createDirectories(sourceRoot.resolve("Album"));
            Path firstTrack = Files.writeString(albumDir.resolve("01 - Intro.mp3"), "x");
            Path secondTrack = Files.writeString(albumDir.resolve("02 - Outro.mp3"), "x");

            JobCoordinator jobCoordinator = new JobCoordinator();
            ImportJobPipeline pipeline = new ImportJobPipeline(
                    new FileScannerService(),
                    new MetadataExtractionService(new TagFallbackParser()),
                    jobCoordinator
            );

            UUID jobId = UUID.fromString("11111111-1111-1111-1111-111111111111");
            ImportJobPipeline.ImportRunResult firstRun = pipeline.run(jobId, sourceRoot, 1);
            ImportJobPipeline.ImportRunResult secondRun = pipeline.run(jobId, sourceRoot, 10);

            assertEquals(List.of(firstTrack), firstRun.processedFiles());
            assertEquals(List.of(secondTrack), secondRun.processedFiles());
            assertEquals("2", secondRun.checkpointToken());
        } finally {
            try (var stream = Files.walk(sourceRoot)) {
                stream.sorted((left, right) -> right.compareTo(left)).forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (Exception ignored) {
                    }
                });
            }
        }
    }
}
