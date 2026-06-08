# Tasks: Organizador e Player de Acervo Musical

**Input**: Design documents from `/specs/001-organizar-acervo-musical/`

**Prerequisites**: plan.md (required), spec.md (required), research.md, data-model.md, contracts/

**Tests**: Test tasks are REQUIRED. Include unit, integration, and contract tests.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and baseline tooling

- [X] T001 Initialize Java 21 project structure in src/ and tests/ with base modules in pom.xml
- [X] T002 Configure application packages in src/domain/, src/application/, src/infrastructure/, src/ui/
- [X] T003 [P] Configure test dependencies and profiles in pom.xml
- [X] T004 [P] Configure code style and static analysis in .editorconfig and pom.xml
- [X] T005 [P] Create runtime configuration template for local paths/providers in src/infrastructure/config/AppConfig.java
- [X] T006 [P] Create bootstrap entrypoint and wiring skeleton in src/ui/AppMain.java

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core shared capabilities that block all user stories

**CRITICAL**: No user story work can begin until this phase is complete

- [X] T007 Implement SQLite connection factory and migrations runner in src/infrastructure/db/DatabaseManager.java
- [X] T008 [P] Create foundational schema migration for core entities in src/infrastructure/db/migrations/V001__core_schema.sql
- [X] T009 [P] Create foundational schema migration for provider/provenance fields in src/infrastructure/db/migrations/V002__providers_provenance.sql
- [X] T010 Implement repository base interfaces and transaction helper in src/infrastructure/db/repository/BaseRepository.java
- [X] T011 Implement filesystem path sanitizer and naming utilities in src/infrastructure/filesystem/PathSanitizer.java
- [X] T012 [P] Implement job orchestration primitives for import/sync checkpoints in src/application/jobs/JobCoordinator.java
- [X] T013 [P] Implement metadata provider gateway interfaces in src/application/metadata/ProviderGateway.java
- [X] T014 Implement tag read/write abstraction (ID3v2->ID3v1 fallback + embed art support) in src/infrastructure/metadata/TagService.java
- [X] T015 [P] Implement global hotkey adapter abstraction in src/infrastructure/player/GlobalHotkeyService.java
- [X] T016 Implement centralized error model and logging policy in src/application/common/AppError.java and src/infrastructure/logging/AppLogger.java

**Checkpoint**: Foundation ready - user story implementation can begin

---

## Phase 3: User Story 1 - Importar, Enriquecer e Organizar Albuns (Priority: P1) MVP

**Goal**: Importar pastas de audio, enriquecer metadados, resolver duplicatas, revisar e organizar saida em MP3

**Independent Test**: Importar uma pasta com MP3 + duplicatas + capa local/online e concluir organizacao validando estrutura final

### Tests for User Story 1 (REQUIRED)


### Implementation for User Story 1

- [X] T022 [P] [US1] Implement file scanner for recursive import input in src/application/importer/FileScannerService.java
- [X] T023 [P] [US1] Implement metadata extraction with fallback strategy in src/application/importer/MetadataExtractionService.java
- [X] T024 [P] [US1] Implement duplicate detection service and candidate metrics in src/application/importer/DuplicateDetectionService.java
- [X] T025 [US1] Implement duplicate resolution application service and persistence in src/application/importer/DuplicateResolutionService.java and src/infrastructure/db/repository/DuplicateResolutionRepository.java
- [X] T026 [US1] Implement import review view-model for per-track decision flow in src/ui/importreview/ImportReviewViewModel.java
- [X] T027 [US1] Implement artwork chooser logic (local vs CAA) in src/application/metadata/ArtworkSelectionService.java
- [X] T028 [US1] Implement organization writer for target layout and filename normalization in src/application/organizer/LibraryOrganizerService.java
- [X] T029 [US1] Implement tag writing including embedded cover art in src/infrastructure/metadata/TagWriteService.java
- [X] T030 [US1] Implement import controller orchestration and finalization guard for unresolved duplicates in src/ui/importreview/ImportController.java

**Checkpoint**: User Story 1 fully functional and independently testable

---

## Phase 4: User Story 2 - Navegacao por Relacionamentos (Priority: P2)

**Goal**: Criar navegacao por Paises, Estilos, Marcacoes e links de cover sem duplicacao de arquivos

**Independent Test**: Organizar album com dados de pais/estilo/cover e verificar links navegaveis em filesystem no Windows/Linux

### Tests for User Story 2 (REQUIRED)

- [X] T031 [P] [US2] Add unit tests for filesystem layout mapping rules in tests/unit/filesystem/LayoutMappingTest.java
- [X] T032 [P] [US2] Add contract test for country/style/tag link generation in tests/contract/filesystem/LinkLayoutContractTest.java
- [X] T033 [P] [US2] Add integration test for cross-OS link regeneration state in tests/integration/filesystem/CrossOsLinkRegenerationIT.java

### Implementation for User Story 2

- [X] T034 [P] [US2] Implement canonical path builder for Paises/Estilos/Marcacoes in src/application/organizer/LayoutPathBuilder.java
- [X] T035 [P] [US2] Implement link creation adapter with OS-aware behavior in src/infrastructure/filesystem/LinkService.java
- [X] T036 [US2] Implement cover relationship link generation in src/application/organizer/CoverLinkService.java
- [X] T037 [US2] Implement style and mark link projection from metadata/tags in src/application/organizer/StyleAndMarkLinkService.java
- [X] T038 [US2] Persist link metadata and provenance in src/infrastructure/db/repository/LinkRepository.java
- [X] T039 [US2] Implement relationship navigation indexing service in src/application/library/NavigationIndexService.java
- [X] T040 [US2] Implement UI refresh for relationship folders in src/ui/library/LibraryNavigationController.java

**Checkpoint**: User Stories 1 and 2 both independently functional

---

## Phase 5: User Story 3 - Player e Historico de Escuta (Priority: P3)

**Goal**: Reproduzir MP3 no app com hotkeys globais e registrar historico de escuta

**Independent Test**: Tocar album completo, pausar via global key e validar persistencia de ultima escuta por faixa/album

### Tests for User Story 3 (REQUIRED)

- [X] T041 [P] [US3] Add unit tests for playback state transitions in tests/unit/player/PlaybackStateMachineTest.java
- [X] T042 [P] [US3] Add integration test for listening event persistence in tests/integration/player/ListeningHistoryIT.java
- [X] T043 [P] [US3] Add contract test for global play/pause handling latency in tests/contract/player/GlobalHotkeyContractTest.java

### Implementation for User Story 3

- [X] T044 [P] [US3] Implement JavaFX MediaPlayer wrapper service in src/infrastructure/player/MediaPlayerService.java
- [X] T045 [US3] Implement playback controller and queue handling in src/application/player/PlaybackController.java
- [X] T046 [US3] Implement listening history recorder in src/application/player/ListeningHistoryService.java
- [X] T047 [US3] Implement hotkey to playback command bridge in src/application/player/HotkeyCommandHandler.java
- [X] T048 [US3] Persist playback events and last-listened projections in src/infrastructure/db/repository/PlaybackRepository.java
- [X] T049 [US3] Implement player UI bindings and actions in src/ui/player/PlayerController.java

**Checkpoint**: User Stories 1, 2 and 3 independently functional

---

## Phase 6: User Story 4 - Curadoria, Recomendacao e Monitoramento Online (Priority: P4)

**Goal**: Permitir tags/notas, recomendacao anti-repeticao e notificacao de albuns ausentes por artista

**Independent Test**: Aplicar tags/notas, rodar sugestao e executar sync periodico com notificacoes de albuns faltantes

### Tests for User Story 4 (REQUIRED)

- [X] T050 [P] [US4] Add unit tests for recommendation score function in tests/unit/recommendation/RecommendationScoreTest.java
- [X] T051 [P] [US4] Add integration test for periodic catalog check workflow in tests/integration/sync/CatalogCheckIT.java
- [X] T052 [P] [US4] Add contract test for multi-provider reconciliation outputs in tests/contract/sync/MultiSourceReconciliationContractTest.java
- [X] T053 [P] [US4] Add integration test for missing-album notification persistence in tests/integration/sync/MissingAlbumNotificationIT.java

### Implementation for User Story 4

- [X] T054 [P] [US4] Implement tag/album rating application services in src/application/library/CurationService.java
- [X] T055 [US4] Implement recommendation service with anti-repetition penalties in src/application/recommendation/RecommendationService.java
- [X] T056 [US4] Implement provider orchestrator for MusicBrainz/Discogs/Wikidata/AcoustID/CAA in src/application/sync/CatalogSyncOrchestrator.java
- [X] T057 [US4] Implement periodic scheduler for artist catalog checks in src/infrastructure/scheduler/CatalogCheckScheduler.java
- [X] T058 [US4] Implement missing album detection and notification service in src/application/sync/MissingAlbumService.java
- [X] T059 [US4] Persist provider references, confidence, and notification records in src/infrastructure/db/repository/CatalogSyncRepository.java
- [X] T060 [US4] Implement recommendation and notifications UI panels in src/ui/library/RecommendationsController.java
- [X] T061 [US4] Integrate curation inputs into player/library flows in src/ui/library/CurationController.java

**Checkpoint**: All user stories independently functional

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Hardening, validation, and final consistency

- [X] T062 [P] Add documentation updates and architecture notes in specs/001-organizar-acervo-musical/quickstart.md and docs/architecture.md
- [X] T063 Run end-to-end quickstart validation scenarios in specs/001-organizar-acervo-musical/quickstart.md
- [X] T064 [P] Add regression tests for duplicate decision replay and re-import behavior in tests/integration/importer/DuplicateReplayIT.java
- [X] T065 Validate performance budgets (import throughput, sync SLA, hotkey latency) in tests/integration/performance/PerformanceBudgetIT.java
- [X] T066 [P] Perform UX consistency review checklist updates in specs/001-organizar-acervo-musical/checklists/requirements.md
- [X] T067 Security and resilience hardening for provider errors and filesystem failures in src/application/common/ResiliencePolicy.java

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: no dependencies
- **Phase 2 (Foundational)**: depends on Phase 1 and blocks all user stories
- **Phase 3 (US1)**: depends on Phase 2
- **Phase 4 (US2)**: depends on Phase 2 (integrates outputs from US1 paths)
- **Phase 5 (US3)**: depends on Phase 2
- **Phase 6 (US4)**: depends on Phase 2 (can run with synthetic history, benefits from US3 data)
- **Phase 7 (Polish)**: depends on completed target user stories

### User Story Dependencies

- **US1 (P1)**: independent after foundation; delivers MVP
- **US2 (P2)**: independent after foundation; uses organized outputs and metadata
- **US3 (P3)**: independent after foundation; uses organized library for playback
- **US4 (P4)**: independent after foundation; recommendation quality improves with US3 history

### Within Each User Story

- Tests first and failing before implementation
- Domain/services before UI wiring
- Persistence updates before integration completion
- Story exits only after independent acceptance checks

---

## Parallel Opportunities

- Setup tasks T003-T006 in parallel
- Foundation tasks T008, T009, T012, T013, T015 in parallel
- US1 tests T017-T021 in parallel; US1 services T022-T024 in parallel
- US2 tests T031-T033 in parallel; US2 services T034-T035 in parallel
- US3 tests T041-T043 in parallel; US3 service/persistence tasks T044 and T048 in parallel
- US4 tests T050-T053 in parallel; US4 tasks T054 and T056 can start in parallel
- Polish tasks T062, T064, T066 in parallel

---

## Parallel Example: User Story 1

```bash
# Tests together
Task: "T017 tests/unit/metadata/TagFallbackParserTest.java"
Task: "T018 tests/unit/importer/DuplicateDetectorTest.java"
Task: "T020 tests/contract/metadata/ArtworkSelectionContractTest.java"

# Core services together
Task: "T022 src/application/importer/FileScannerService.java"
Task: "T023 src/application/importer/MetadataExtractionService.java"
Task: "T024 src/application/importer/DuplicateDetectionService.java"
```

## Parallel Example: User Story 4

```bash
# Tests together
Task: "T050 tests/unit/recommendation/RecommendationScoreTest.java"
Task: "T052 tests/contract/sync/MultiSourceReconciliationContractTest.java"

# Services together
Task: "T054 src/application/library/CurationService.java"
Task: "T056 src/application/sync/CatalogSyncOrchestrator.java"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1 (Setup)
2. Complete Phase 2 (Foundational)
3. Complete Phase 3 (US1)
4. Validate import, duplicate resolution, artwork selection, and organization flow
5. Demo/deploy MVP slice

### Incremental Delivery

1. Add US2 for relationship navigation links
2. Add US3 for player + listening history
3. Add US4 for recommendation + periodic missing album checks
4. Finish with Phase 7 hardening and budget validation

### Parallel Team Strategy

1. Team aligns on setup/foundation together
2. After foundation:
   - Dev A: US1 core import/organizer
   - Dev B: US2 filesystem navigation links
   - Dev C: US3 player/hotkeys
   - Dev D: US4 sync/recommendation
3. Integrate on shared contracts and finalize polish

---

## Notes

- [P] tasks denote independent files with no blocking dependency
- [USx] labels map each task to a single user story for traceability
- Avoid auto-merging duplicates without explicit user decision
- Preserve source files; organized output is always derived copy
- Persist provenance for provider data and artwork selection decisions
