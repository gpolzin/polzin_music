package com.polzin.music.application.importer;

import com.polzin.music.infrastructure.db.repository.DuplicateResolutionRepository;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public final class DuplicateResolutionService {

    public record ResolutionDecision(String duplicateGroupId,
                                     String selectedCandidateId,
                                     List<String> discardedCandidateIds,
                                     String decidedBy,
                                     String decisionReason,
                                     Instant decidedAt) {
    }

    private final DuplicateResolutionRepository repository;

    public DuplicateResolutionService(DuplicateResolutionRepository repository) {
        this.repository = repository;
    }

    public ResolutionDecision resolve(ResolutionDecision decision) throws SQLException {
        Objects.requireNonNull(decision, "decision");
        if (decision.duplicateGroupId() == null || decision.duplicateGroupId().isBlank()) {
            throw new IllegalArgumentException("duplicateGroupId must not be blank");
        }
        if (decision.selectedCandidateId() == null || decision.selectedCandidateId().isBlank()) {
            throw new IllegalArgumentException("selectedCandidateId must not be blank");
        }
        repository.save(new DuplicateResolutionRepository.DuplicateResolutionRecord(
                decision.duplicateGroupId(),
                decision.selectedCandidateId(),
                decision.discardedCandidateIds(),
                decision.decidedBy(),
                decision.decisionReason(),
                decision.decidedAt() == null ? Instant.now() : decision.decidedAt()
        ));
        return decision;
    }
}
