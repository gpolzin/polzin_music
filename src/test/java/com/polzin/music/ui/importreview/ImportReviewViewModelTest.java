package com.polzin.music.ui.importreview;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImportReviewViewModelTest {

    @Test
    void tracksDecisionsAndUnresolvedGroups() {
        ImportReviewViewModel viewModel = new ImportReviewViewModel();
        viewModel.loadCandidates(List.of(
                new ImportReviewViewModel.TrackCandidate("group-1", "candidate-a", "Faixa 1", "Artista"),
                new ImportReviewViewModel.TrackCandidate("group-1", "candidate-b", "Faixa 1", "Artista"),
                new ImportReviewViewModel.TrackCandidate("group-2", "candidate-c", "Faixa 2", "Artista")
        ));

        assertFalse(viewModel.isComplete());
        assertEquals(2, viewModel.unresolvedGroups().size());

        viewModel.chooseCandidate("group-1", "candidate-b", "best quality");

        assertEquals(1, viewModel.decisions().size());
        assertTrue(viewModel.unresolvedGroups().contains("group-2"));
    }
}
