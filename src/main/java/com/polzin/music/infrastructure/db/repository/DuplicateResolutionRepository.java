package com.polzin.music.infrastructure.db.repository;

import com.polzin.music.infrastructure.db.DatabaseManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public final class DuplicateResolutionRepository extends BaseRepository {

    public record DuplicateResolutionRecord(String duplicateGroupId,
                                            String selectedCandidateId,
                                            List<String> discardedCandidateIds,
                                            String decidedBy,
                                            String decisionReason,
                                            Instant decidedAt) {
    }

    public DuplicateResolutionRepository(DatabaseManager databaseManager) {
        super(databaseManager);
    }

    public void save(DuplicateResolutionRecord record) throws SQLException {
        withTransaction(connection -> {
            try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO duplicate_resolutions (
                        id, duplicate_group_id, selected_candidate_id, discarded_candidate_ids,
                        decided_by, decision_reason, decided_at
                    ) VALUES (?, ?, ?, ?, ?, ?, ?)
                    ON CONFLICT(duplicate_group_id) DO UPDATE SET
                        selected_candidate_id = excluded.selected_candidate_id,
                        discarded_candidate_ids = excluded.discarded_candidate_ids,
                        decided_by = excluded.decided_by,
                        decision_reason = excluded.decision_reason,
                        decided_at = excluded.decided_at
                    """);) {
                statement.setString(1, record.duplicateGroupId());
                statement.setString(2, record.duplicateGroupId());
                statement.setString(3, record.selectedCandidateId());
                statement.setString(4, String.join(";", record.discardedCandidateIds()));
                statement.setString(5, record.decidedBy());
                statement.setString(6, record.decisionReason());
                statement.setString(7, record.decidedAt().toString());
                statement.executeUpdate();
            }
        });
    }

    public Optional<DuplicateResolutionRecord> findByGroupId(String duplicateGroupId) throws SQLException {
        return withConnection(connection -> {
            try (PreparedStatement statement = connection.prepareStatement("""
                    SELECT duplicate_group_id, selected_candidate_id, discarded_candidate_ids,
                           decided_by, decision_reason, decided_at
                    FROM duplicate_resolutions
                    WHERE duplicate_group_id = ?
                    """)) {
                statement.setString(1, duplicateGroupId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) {
                        return Optional.empty();
                    }
                    return Optional.of(new DuplicateResolutionRecord(
                            resultSet.getString("duplicate_group_id"),
                            resultSet.getString("selected_candidate_id"),
                            parseDiscarded(resultSet.getString("discarded_candidate_ids")),
                            resultSet.getString("decided_by"),
                            resultSet.getString("decision_reason"),
                            Instant.parse(resultSet.getString("decided_at"))
                    ));
                }
            }
        });
    }

    private List<String> parseDiscarded(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return List.of(value.split(";"));
    }
}
