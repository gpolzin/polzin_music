package com.polzin.music.integration.performance;

import com.polzin.music.application.importer.FileScannerService;
import com.polzin.music.application.importer.ImportJobPipeline;
import com.polzin.music.application.importer.MetadataExtractionService;
import com.polzin.music.application.importer.TagFallbackParser;
import com.polzin.music.application.jobs.JobCoordinator;
import com.polzin.music.application.player.HotkeyCommandHandler;
import com.polzin.music.application.player.PlaybackController;
import com.polzin.music.application.player.PlaybackStateMachine;
import com.polzin.music.application.recommendation.RecommendationService;
import com.polzin.music.application.sync.CatalogSyncOrchestrator;
import com.polzin.music.infrastructure.player.GlobalHotkeyService;
import com.polzin.music.infrastructure.player.MediaPlayerService;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTimeout;

class PerformanceBudgetIT {

    @Test
    void meetsImportSyncAndHotkeyBudgets() throws Exception {
        assertTimeout(Duration.ofSeconds(2), this::measureImportThroughput);
        assertTimeout(Duration.ofSeconds(2), this::measureSyncSla);
        assertTimeout(Duration.ofSeconds(1), this::measureHotkeyLatency);
    }

    private void measureImportThroughput() throws Exception {
        Path root = Files.createTempDirectory("polzin-performance-import");
        try {
            Path albumDir = Files.createDirectories(root.resolve("Album"));
            for (int index = 1; index <= 120; index++) {
                Files.writeString(albumDir.resolve(String.format("%02d - Track %03d.mp3", index, index)), "x");
            }

            ImportJobPipeline pipeline = new ImportJobPipeline(
                    new FileScannerService(),
                    new MetadataExtractionService(new TagFallbackParser()),
                    new JobCoordinator()
            );

            pipeline.run(UUID.fromString("22222222-2222-2222-2222-222222222222"), root, 120);
        } finally {
            try (var stream = Files.walk(root)) {
                stream.sorted((left, right) -> right.compareTo(left)).forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (Exception ignored) {
                    }
                });
            }
        }
    }

    private void measureSyncSla() {
        CatalogSyncOrchestrator orchestrator = new CatalogSyncOrchestrator();
        List<CatalogSyncOrchestrator.CatalogEntry> remoteEntries = java.util.stream.IntStream.range(0, 180)
                .mapToObj(index -> new CatalogSyncOrchestrator.CatalogEntry(
                        index % 2 == 0 ? "MUSICBRAINZ" : "DISCOGS",
                        "external-" + index,
                        "canonical-" + (index / 2),
                        "Album " + index,
                        "STUDIO",
                        2020 + (index % 5),
                        0.80 + (index % 10) * 0.01
                ))
                .toList();

        List<String> localOwned = java.util.stream.IntStream.range(0, 45)
                .mapToObj(index -> "Album " + (index * 2))
                .toList();

        orchestrator.reconcile("artist-performance", remoteEntries, localOwned);
    }

    private void measureHotkeyLatency() {
        PlaybackStateMachine playbackStateMachine = new PlaybackStateMachine();
        MediaPlayerService mediaPlayerService = new MediaPlayerService() {
            @Override
            public void play(Path audioFile) {
            }

            @Override
            public void pause() {
            }

            @Override
            public void stop() {
            }

            @Override
            public boolean isPlaying() {
                return false;
            }
        };
        PlaybackController playbackController = new PlaybackController(playbackStateMachine, mediaPlayerService);
        GlobalHotkeyService hotkeyService = new GlobalHotkeyService() {
            @Override
            public void registerPlayPause(Runnable action) {
                action.run();
            }

            @Override
            public void unregisterAll() {
            }
        };

        HotkeyCommandHandler handler = new HotkeyCommandHandler(hotkeyService, playbackController);
        handler.registerPlayPauseHotkey();
        handler.unregisterAll();
    }
}
