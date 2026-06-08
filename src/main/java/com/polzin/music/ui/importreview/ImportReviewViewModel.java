package com.polzin.music.ui.importreview;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class ImportReviewViewModel {

    public record TrackCandidate(String duplicateGroupId, String candidateId, String title, String artist) {
    }

    public record TrackDecision(String duplicateGroupId, String selectedCandidateId, String reason) {
    }

    private final Map<String, List<TrackCandidate>> candidatesByGroup = new LinkedHashMap<>();
    private final Map<String, TrackDecision> decisionsByGroup = new LinkedHashMap<>();

    public void loadCandidates(List<TrackCandidate> candidates) {
        candidatesByGroup.clear();
        decisionsByGroup.clear();
        if (candidates == null) {
            return;
        }
        for (TrackCandidate candidate : candidates) {
            candidatesByGroup.computeIfAbsent(candidate.duplicateGroupId(), key -> new ArrayList<>()).add(candidate);
        }
    }

    public List<TrackCandidate> candidatesForGroup(String duplicateGroupId) {
        return List.copyOf(candidatesByGroup.getOrDefault(duplicateGroupId, List.of()));
    }

    public void chooseCandidate(String duplicateGroupId, String candidateId, String reason) {
        Objects.requireNonNull(duplicateGroupId, "duplicateGroupId");
        Objects.requireNonNull(candidateId, "candidateId");
        decisionsByGroup.put(duplicateGroupId, new TrackDecision(duplicateGroupId, candidateId, reason));
    }

    public List<TrackDecision> decisions() {
        return List.copyOf(decisionsByGroup.values());
    }

    public List<String> unresolvedGroups() {
        Set<String> unresolved = candidatesByGroup.keySet().stream()
                .filter(groupId -> !decisionsByGroup.containsKey(groupId))
                .collect(Collectors.toSet());
        return unresolved.stream().sorted().toList();
    }

    public boolean isComplete() {
        return unresolvedGroups().isEmpty() && !candidatesByGroup.isEmpty();
    }
}
