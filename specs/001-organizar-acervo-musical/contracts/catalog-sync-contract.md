# Contract - Catalog Sync and Missing Albums

## Purpose
Define the interface for periodic online catalog verification per artist and missing-album notifications.

## Provider
- Canonical provider: MusicBrainz (entity identity and relationships)
- Complementary providers:
  - Discogs (discography completeness and release variants)
  - Wikidata (country and aliases normalization)
  - AcoustID (fingerprint-based track identity fallback)
  - Cover Art Archive (album artwork metadata)

## Provider Capability Contract
- MUSICBRAINZ:
  - Required capabilities: canonical IDs, release groups/releases, work/recording relationships, cover-of lookup
- DISCOGS:
  - Required capabilities: artist discography listing, release-year/title lookup
- WIKIDATA:
  - Required capabilities: country of origin and alias enrichment
- ACOUSTID:
  - Required capabilities: track fingerprint lookup when tags are low confidence
- COVER_ART_ARCHIVE:
  - Required capabilities: artwork availability and image metadata

## Artwork Selection Contract
- Artwork sources:
  - LOCAL_FOLDER_IMAGE: image discovered in source folder (preferred fallback when available)
  - COVER_ART_ARCHIVE: remote artwork when local image is absent or user prefers remote
- Review UI must present at least one selectable artwork candidate when available.
- Selected artwork must be persisted as album artwork metadata and passed to the tag-writing step
  so the chosen image is embedded into the organized MP3 files.
- If no artwork is available, proceed without blocking import, but mark album as missing artwork.

## Input Contract
- artist_id: UUID (required)
- artist_name: string (required)
- providers: array<enum { MUSICBRAINZ, DISCOGS, WIKIDATA, ACOUSTID, COVER_ART_ARCHIVE }> (required)
- canonical_provider: enum { MUSICBRAINZ } (required)
- last_check_at: datetime (optional)
- force_refresh: boolean (optional, default false)

## Retrieval Rules
1. Query canonical identity and baseline discography from MUSICBRAINZ.
2. Query DISCOGS as fallback/complement for missing or conflicting release entries.
3. Query WIKIDATA for country/alias normalization of the matched artist.
4. Use ACOUSTID only when track identity confidence from tags is below threshold.
5. Query COVER_ART_ARCHIVE for artwork metadata linked to resolved canonical release/release-group.
6. Normalize album identity by canonical key: canonical_artist_id + normalized_title + release_year + album_type.
7. Compare unified remote catalog against local albums owned in MP3.
8. Emit missing album records for non-owned entries.

## Reconciliation Policy Contract
- Canonical precedence order:
  1. MUSICBRAINZ (authoritative identity)
  2. DISCOGS (supplementary catalog entries)
  3. WIKIDATA (supplementary context)
- Conflict resolution:
  - Keep canonical IDs from MUSICBRAINZ when available.
  - Persist external IDs from all matched providers.
  - Compute `confidence` per match in range 0..1.
  - Mark unresolved conflicts for user review when confidence < 0.75.
- Provenance:
  - Every enriched field must store `source_provider`, `source_reference`, and `resolved_at`.

## Output Contract
- status: enum { OK, RATE_LIMITED, FAILED }
- checked_at: datetime
- coverage_ratio: decimal (0..1)
- artwork_candidates: array
  - source: enum { LOCAL_FOLDER_IMAGE, COVER_ART_ARCHIVE }
  - reference: string
  - mime_type: string
  - selected_by_user: boolean
- missing_albums: array
  - canonical_reference: string
  - provider_references: array<{ provider: enum, reference: string }>
  - title: string
  - album_type: enum { STUDIO, COMPILATION, SINGLE, EP, LIVE, OTHER }
  - release_year: integer|null
  - confidence: decimal (0..1)
  - source_priority_used: array<enum { MUSICBRAINZ, DISCOGS, WIKIDATA, ACOUSTID, COVER_ART_ARCHIVE }>
  - needs_user_review: boolean

## Error Contract
- RATE_LIMITED: retry_after_seconds required
- FAILED: message required, retry_policy suggested

## Rate and Terms Contract
- Per-provider limits must be independently configurable.
- Requests must identify client application per provider policy when required.
- Caching and redistribution must follow each provider terms/license.
- If a provider is unavailable or blocked by quota, continue pipeline with remaining providers.

## Scheduling Contract
- check_frequency: user-configurable duration (default: 7 days)
- max_artists_per_cycle: integer > 0
- rate_limit_budget_per_minute: map<provider, integer> with values > 0
- provider_timeout_seconds: map<provider, integer> with values > 0

## Acceptance Notes
- Must cover at least 95% of artists each cycle (SC-010).
- Notification list must be available <= 10 minutes after cycle end (SC-010).
- Multi-source reconciliation must preserve canonical identity and provenance for auditability.
- Selected artwork provenance must be stored with the album record and reflected in embedded MP3 tags.
