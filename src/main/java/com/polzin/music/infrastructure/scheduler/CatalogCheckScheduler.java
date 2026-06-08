package com.polzin.music.infrastructure.scheduler;

import com.polzin.music.application.sync.CatalogSyncOrchestrator;
import com.polzin.music.application.sync.MissingAlbumService;

import java.util.List;

public final class CatalogCheckScheduler {

    public record CatalogCheckTask(MissingAlbumService.MissingAlbumCheck check) {
    }

    private final MissingAlbumService missingAlbumService;

    public CatalogCheckScheduler(MissingAlbumService missingAlbumService) {
        this.missingAlbumService = missingAlbumService;
    }

    public List<CatalogSyncOrchestrator.CatalogSyncResult> runChecks(List<CatalogCheckTask> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return List.of();
        }
        return tasks.stream()
                .map(task -> missingAlbumService.detectAndPersist(task.check()))
                .toList();
    }
}
