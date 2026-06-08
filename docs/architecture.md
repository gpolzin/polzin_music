# Architecture Notes

## Overview
The project uses a layered architecture that keeps business rules separate from infrastructure and UI concerns.

## Layers

### Application
- Coordinates import, library organization, playback, recommendations, curation, and sync workflows.
- Contains stateful services only when the behavior is part of the domain flow.

### Infrastructure
- Handles database access, filesystem operations, scheduling, logging, and external provider integration.
- Encapsulates persistence and platform-specific behavior behind small focused components.

### UI
- Exposes user actions and view-facing controllers.
- Delegates directly to application services without duplicating business rules.

## Key Cross-Cutting Decisions
- Duplicate resolutions are persisted so re-imports can replay previous user choices.
- Catalog sync stores provenance and missing-album notifications for later review.
- Recommendation scoring penalizes repeated recent listens to keep suggestions varied.
- Resilience policy wraps provider and filesystem failures to avoid leaking low-level error details into the UI.

## Validation Strategy
- Import flows are covered by regression tests that exercise resume/replay behavior.
- Sync flows are covered by integration tests for periodic checks and missing-album notifications.
- Performance budgets are checked against import throughput, catalog reconciliation, and hotkey latency.
