# Contract - Duplicate Resolution During Import

## Purpose
Define how duplicate tracks are detected, compared, presented to the user, and resolved before final organization.

## Detection Contract
- Candidate duplicates must be grouped during import when one or more of these signals match:
  - normalized title
  - normalized artist
  - close duration window (configurable, default +/- 3s)
  - same recording ID when available (e.g., MusicBrainz)
- Detection must avoid merging tracks from clearly distinct versions (e.g., live vs studio) unless user confirms.

## Comparison Contract
For each duplicate candidate, the UI must show at least:
- source_file_path
- file_size_bytes
- bitrate_kbps (if available)
- sample_rate_hz (if available)
- duration_seconds (if available)
- codec/extension
- metadata source confidence (if available)

## Resolution Contract
- Resolution mode: track-by-track user decision.
- Allowed actions per group:
  - KEEP_ONE_DISCARD_OTHERS
  - KEEP_MULTIPLE (optional, if user wants coexistence)
  - SKIP_FOR_LATER
- A group cannot be marked resolved without explicit selected candidate(s).

## Persistence Contract
- Every decision must be persisted with:
  - duplicate_group_id
  - selected_candidate_id(s)
  - discarded_candidate_id(s)
  - decided_at
  - decided_by
  - decision_reason (optional)

## Organization Contract
- Only selected candidates proceed to final organized output.
- Discarded candidates remain untouched in source and are excluded from output copy.
- If unresolved groups remain, finalization should block or require explicit user override.

## UX Contract
- Comparison screen must support quick switching between candidates.
- Quality metrics must be visible without opening extra dialogs.
- User should be able to apply a decision pattern to similar groups (optional accelerator).

## Acceptance Notes
- At least 95% of known duplicate groups in test dataset must be detected and presented with complete comparison metrics (SC-014).
- Resolution history must be auditable for re-import and rollback analysis.
