package com.polzin.music.infrastructure.db.repository;

import com.polzin.music.infrastructure.db.DatabaseManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public final class PlaybackRepository extends BaseRepository {

    public record PlaybackEventRecord(UUID id,
                                      UUID trackId,
                                      UUID albumId,
                                      Instant startedAt,
                                      Instant endedAt,
                                      double listenedRatio,
                                      String source) {
    }

    public PlaybackRepository(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    public void save(PlaybackEventRecord record) throws SQLException {
        withTransaction(connection -> {
            try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO playback_events (id, track_id, album_id, started_at, ended_at, listened_ratio, source)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """)) {
                statement.setString(1, record.id().toString());
                statement.setString(2, record.trackId().toString());
                statement.setString(3, record.albumId().toString());
                statement.setString(4, record.startedAt().toString());
                statement.setString(5, record.endedAt() == null ? null : record.endedAt().toString());
                statement.setDouble(6, record.listenedRatio());
                statement.setString(7, record.source());
                statement.executeUpdate();
            }

            try (PreparedStatement trackStatement = connection.prepareStatement("""
                    UPDATE tracks
                    SET last_listened_at = ?
                    WHERE id = ?
                    """)) {
                trackStatement.setString(1, record.endedAt() == null ? record.startedAt().toString() : record.endedAt().toString());
                trackStatement.setString(2, record.trackId().toString());
                trackStatement.executeUpdate();
            }

            try (PreparedStatement albumStatement = connection.prepareStatement("""
                    UPDATE albums
                    SET last_listened_at = ?
                    WHERE id = ?
                    """)) {
                albumStatement.setString(1, record.endedAt() == null ? record.startedAt().toString() : record.endedAt().toString());
                albumStatement.setString(2, record.albumId().toString());
                albumStatement.executeUpdate();
            }
        });
    }

    public Optional<PlaybackEventRecord> findLatestByTrackId(UUID trackId) throws SQLException {
        return withConnection(connection -> {
            try (PreparedStatement statement = connection.prepareStatement("""
                    SELECT id, track_id, album_id, started_at, ended_at, listened_ratio, source
                    FROM playback_events
                    WHERE track_id = ?
                    ORDER BY started_at DESC
                    LIMIT 1
                    """)) {
                statement.setString(1, trackId.toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) {
                        return Optional.empty();
                    }
                    return Optional.of(new PlaybackEventRecord(
                            UUID.fromString(resultSet.getString("id")),
                            UUID.fromString(resultSet.getString("track_id")),
                            UUID.fromString(resultSet.getString("album_id")),
                            Instant.parse(resultSet.getString("started_at")),
                            resultSet.getString("ended_at") == null ? null : Instant.parse(resultSet.getString("ended_at")),
                            resultSet.getDouble("listened_ratio"),
                            resultSet.getString("source")
                    ));
                }
            }
        });
    }
}
