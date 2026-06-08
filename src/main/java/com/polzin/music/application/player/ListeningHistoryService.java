package com.polzin.music.application.player;

import com.polzin.music.infrastructure.db.repository.PlaybackRepository;

import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public final class ListeningHistoryService {

    public record ListeningSession(UUID trackId,
                                   UUID albumId,
                                   Instant startedAt,
                                   Instant endedAt,
                                   double listenedRatio,
                                   String source) {
    }

    private final PlaybackRepository playbackRepository;

    public ListeningHistoryService(PlaybackRepository playbackRepository) {
        this.playbackRepository = playbackRepository;
    }

    public ListeningSession record(ListeningSession session) throws SQLException {
        playbackRepository.save(new PlaybackRepository.PlaybackEventRecord(
                UUID.randomUUID(),
                session.trackId(),
                session.albumId(),
                session.startedAt(),
                session.endedAt(),
                session.listenedRatio(),
                session.source()
        ));
        return session;
    }
}
