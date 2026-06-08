package com.polzin.music.application.importer;

import com.polzin.music.application.jobs.JobCoordinator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ImportJobPipeline {

    public record ImportRunResult(List<Path> processedFiles, String checkpointToken) {
    }

    private final FileScannerService fileScannerService;
    private final MetadataExtractionService metadataExtractionService;
    private final JobCoordinator jobCoordinator;

    public ImportJobPipeline(FileScannerService fileScannerService,
                             MetadataExtractionService metadataExtractionService,
                             JobCoordinator jobCoordinator) {
        this.fileScannerService = fileScannerService;
        this.metadataExtractionService = metadataExtractionService;
        this.jobCoordinator = jobCoordinator;
    }

    public ImportRunResult run(UUID jobId, Path sourceRoot, int maxFilesToProcess) throws IOException {
        List<Path> files = fileScannerService.scan(sourceRoot);
        int startIndex = readResumeIndex(jobId);
        int limit = Math.max(0, maxFilesToProcess);
        List<Path> processedFiles = new ArrayList<>();
        int processedCount = 0;

        for (int index = startIndex; index < files.size(); index++) {
            if (processedCount >= limit) {
                break;
            }
            Path file = files.get(index);
            metadataExtractionService.extract(file, List.of(), List.of());
            processedFiles.add(file);
            processedCount++;
            jobCoordinator.updateCheckpoint(jobId, Integer.toString(index + 1));
        }

        JobCoordinator.JobCheckpoint checkpoint = jobCoordinator.getCheckpoint(jobId);
        String checkpointToken = checkpoint == null ? null : checkpoint.checkpointToken();
        return new ImportRunResult(List.copyOf(processedFiles), checkpointToken);
    }

    private int readResumeIndex(UUID jobId) {
        JobCoordinator.JobCheckpoint checkpoint = jobCoordinator.getCheckpoint(jobId);
        if (checkpoint == null || checkpoint.checkpointToken() == null || checkpoint.checkpointToken().isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(checkpoint.checkpointToken());
        } catch (NumberFormatException exception) {
            return 0;
        }
    }
}
