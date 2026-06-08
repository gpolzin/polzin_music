package com.polzin.music.ui.importreview;

import com.polzin.music.application.importer.DuplicateResolutionService;

import java.sql.SQLException;
import java.time.Instant;

public final class ImportController {

    private final ImportReviewViewModel viewModel;
    private final DuplicateResolutionService duplicateResolutionService;

    public ImportController(ImportReviewViewModel viewModel, DuplicateResolutionService duplicateResolutionService) {
        this.viewModel = viewModel;
        this.duplicateResolutionService = duplicateResolutionService;
    }

    public void chooseCandidate(String duplicateGroupId, String candidateId, String reason) {
        viewModel.chooseCandidate(duplicateGroupId, candidateId, reason);
    }

    public void finalizeImport(String decidedBy) throws SQLException {
        if (!viewModel.unresolvedGroups().isEmpty()) {
            throw new IllegalStateException("Cannot finalize import while duplicate groups remain unresolved");
        }
        for (ImportReviewViewModel.TrackDecision decision : viewModel.decisions()) {
            duplicateResolutionService.resolve(new DuplicateResolutionService.ResolutionDecision(
                    decision.duplicateGroupId(),
                    decision.selectedCandidateId(),
                    java.util.List.of(),
                    decidedBy,
                    decision.reason(),
                    Instant.now()
            ));
        }
    }
}
