package com.polzin.music.application.jobs;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class JobCoordinator {

    public record JobCheckpoint(UUID jobId, String checkpointToken, Instant updatedAt) {
    }

    private final Map<UUID, JobCheckpoint> checkpoints = new ConcurrentHashMap<>();

    public JobCheckpoint updateCheckpoint(UUID jobId, String checkpointToken) {
        JobCheckpoint checkpoint = new JobCheckpoint(jobId, checkpointToken, Instant.now());
        checkpoints.put(jobId, checkpoint);
        return checkpoint;
    }

    public JobCheckpoint getCheckpoint(UUID jobId) {
        return checkpoints.get(jobId);
    }
}
