package com.polzin.music.application.importer;

import com.polzin.music.application.jobs.JobCoordinator;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImportJobPipelineIT {

    @Test
    void resumesFromCheckpointAfterPartialProcessing() throws Exception {
        Path sourceRoot = Files.createTempDirectory("polzin-import-pipeline");
        try {
            Path album = Files.createDirectories(sourceRoot.resolve("Album"));
            Files.writeString(album.resolve("01 - Intro.mp3"), "a");
            Files.writeString(album.resolve("02 - Middle.mp3"), "b");
            Files.writeString(album.resolve("03 - Finale.mp3"), "c");

            JobCoordinator jobCoordinator = new JobCoordinator();
            ImportJobPipeline pipeline = new ImportJobPipeline(
                    new FileScannerService(),
                    new MetadataExtractionService(new TagFallbackParser()),
                    jobCoordinator
            );

            UUID jobId = UUID.fromString("11111111-1111-1111-1111-111111111111");

            ImportJobPipeline.ImportRunResult firstRun = pipeline.run(jobId, sourceRoot, 1);
            assertEquals(1, firstRun.processedFiles().size());
            assertEquals("1", firstRun.checkpointToken());

            ImportJobPipeline.ImportRunResult secondRun = pipeline.run(jobId, sourceRoot, 10);
            assertEquals(2, secondRun.processedFiles().size());
            assertEquals("3", secondRun.checkpointToken());
            assertTrue(secondRun.processedFiles().getFirst().getFileName().toString().contains("02 - Middle"));
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
