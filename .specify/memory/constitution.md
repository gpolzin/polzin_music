<!--
Sync Impact Report
Version change: 0.0.0 -> 1.0.0
Modified principles:
- Placeholder PRINCIPLE_1_NAME -> I. Code Quality Is Non-Negotiable
- Placeholder PRINCIPLE_2_NAME -> II. Tests Define Done
- Placeholder PRINCIPLE_3_NAME -> III. User Experience Consistency By Default
- Placeholder PRINCIPLE_4_NAME -> IV. Performance Budgets Are Product Requirements
- Placeholder PRINCIPLE_5_NAME -> V. Traceable Engineering Decisions
Added sections:
- Engineering Standards
- Delivery Workflow & Quality Gates
Removed sections:
- None
Templates requiring updates:
- ✅ .specify/templates/plan-template.md
- ✅ .specify/templates/spec-template.md
- ✅ .specify/templates/tasks-template.md
- ⚠ pending .specify/templates/commands/*.md (directory not present)
Follow-up TODOs:
- None
-->

# polzin_music Constitution

## Core Principles

### I. Code Quality Is Non-Negotiable
All production code MUST be readable, intentional, and maintainable. Every change MUST
include clear naming, bounded function complexity, and documentation for non-obvious
decisions. Linting and formatting MUST pass before merge. Rationale: high code quality
reduces defect density and preserves delivery speed as the codebase grows.

### II. Tests Define Done
A change is complete only when automated tests validate expected behavior and critical
failure paths. Every feature MUST include unit tests, and integration tests MUST be added
for cross-module workflows, API contracts, or user-critical journeys. Bug fixes MUST add a
regression test that fails before the fix and passes after. Rationale: mandatory tests are
the primary defense against regressions.

### III. User Experience Consistency By Default
User-facing behavior MUST follow consistent interaction patterns, language tone,
accessibility basics, and visual conventions across screens and flows. New UI work MUST
reuse established design primitives unless a documented exception is approved. Rationale:
consistency lowers user cognitive load and improves trust.

### IV. Performance Budgets Are Product Requirements
Features MUST define measurable performance targets before implementation and verify them
before release. At minimum, each feature MUST specify latency and/or rendering goals,
resource constraints, and acceptable degradation behavior under load. Regressions against
agreed budgets MUST block release unless explicitly waived. Rationale: performance is a
core part of product quality, not a post-release optimization.

### V. Traceable Engineering Decisions
Plans, specs, tasks, and pull requests MUST explicitly trace implementation choices back to
requirements, principles, and constraints. Trade-offs and exceptions MUST be documented with
owner and rationale. Rationale: traceability enables faster reviews, easier maintenance,
and accountable governance.

## Engineering Standards

- Every feature spec MUST include measurable success criteria for quality, UX, and
	performance.
- Every implementation plan MUST include enforceable constitution gates and concrete
	verification methods.
- Task breakdowns MUST include explicit testing, UX validation, and performance validation
	work items.
- Any temporary exception MUST include scope, owner, expiry date, and rollback/cleanup plan.

## Delivery Workflow & Quality Gates

- Gate 1 (Plan): constitution checks MUST be written as pass/fail items before design begins.
- Gate 2 (Build): code quality checks and required tests MUST pass in CI.
- Gate 3 (Review): reviewers MUST verify UX consistency and documented performance evidence.
- Gate 4 (Release): performance budgets and regression checks MUST be satisfied or formally
	waived with sign-off.

## Governance

This constitution is the highest-priority engineering policy for this repository. In case of
conflict, this document overrides local habits and ad hoc process notes.

Amendment procedure:
1. Propose amendment with rationale and impacted templates.
2. Update dependent templates and guidance files in the same change.
3. Record version bump rationale using semantic versioning rules.
4. Obtain maintainer approval before merge.

Versioning policy:
- MAJOR: incompatible principle removals/redefinitions or governance model changes.
- MINOR: new principle/section or materially expanded mandatory guidance.
- PATCH: wording clarifications, typo fixes, or non-semantic refinements.

Compliance review expectations:
- Every plan and PR MUST include an explicit constitution compliance check.
- Any waiver MUST be documented with risk, owner, and expiration.
- Periodic audits MAY be run; unresolved violations MUST be tracked to closure.

**Version**: 1.0.0 | **Ratified**: 2026-06-08 | **Last Amended**: 2026-06-08
