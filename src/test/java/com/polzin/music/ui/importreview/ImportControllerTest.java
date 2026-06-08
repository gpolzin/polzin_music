package com.polzin.music.ui.importreview;

import com.polzin.music.application.importer.DuplicateResolutionService;
import com.polzin.music.infrastructure.db.DatabaseManager;
import com.polzin.music.infrastructure.db.repository.DuplicateResolutionRepository;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ImportControllerTest {

    @Test
    void blocksFinalizationWhileAnyGroupRemainsOpen() throws Exception {
        Path databaseFile = Files.createTempFile("polzin-import-controller", ".db");
        try (DatabaseManager databaseManager = new DatabaseManager("jdbc:sqlite:" + databaseFile.toAbsolutePath())) {
            databaseManager.initialize();
            DuplicateResolutionRepository repository = new DuplicateResolutionRepository(databaseManager);
            DuplicateResolutionService service = new DuplicateResolutionService(repository);
            ImportReviewViewModel viewModel = new ImportReviewViewModel();
            viewModel.loadCandidates(List.of(
                    new ImportReviewViewModel.TrackCandidate("group-1", "candidate-a", "Faixa 1", "Artista"),
                    new ImportReviewViewModel.TrackCandidate("group-1", "candidate-b", "Faixa 1", "Artista"),
                    new ImportReviewViewModel.TrackCandidate("group-2", "candidate-c", "Faixa 2", "Artista")
            ));

            try (Connection connection = databaseManager.openConnection()) {
                connection.createStatement().executeUpdate("""
                        INSERT INTO import_jobs (id, source_root, destination_root, status)
                        VALUES ('job-3', 'source', 'dest', 'RUNNING')
                        """);
                connection.createStatement().executeUpdate("""
                        INSERT INTO duplicate_groups (id, import_job_id, status)
                        VALUES ('group-1', 'job-3', 'OPEN')
                        """);
                connection.createStatement().executeUpdate("""
                        INSERT INTO duplicate_groups (id, import_job_id, status)
                        VALUES ('group-2', 'job-3', 'OPEN')
                        """);
            }

            ImportController controller = new ImportController(viewModel, service);
            controller.chooseCandidate("group-1", "candidate-b", "best quality");

            assertThrows(IllegalStateException.class, () -> controller.finalizeImport("USER"));

            controller.chooseCandidate("group-2", "candidate-c", "keep coexistence");
            assertDoesNotThrow(() -> controller.finalizeImport("USER"));
            Instant ignored = repository.findByGroupId("group-1").orElseThrow().decidedAt();
        } finally {
            Files.deleteIfExists(databaseFile);
        }
    }
}
