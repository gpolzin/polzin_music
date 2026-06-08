# Data Model - Organizador e Player de Acervo Musical

## Artist
- Fields:
  - id (UUID)
  - name (string, required)
  - musicbrainz_id (string, optional, unique when present)
  - country_code (string, ISO-3166-1 alpha-2, optional)
  - aliases (string[])
  - created_at, updated_at (datetime)
- Relationships:
  - 1:N with Album
  - 1:N with Track (main artist)
- Validation rules:
  - name cannot be blank
  - country_code must be valid ISO code if present

## Album
- Fields:
  - id (UUID)
  - artist_id (FK Artist, required)
  - title (string, required)
  - album_type (enum: STUDIO, COMPILATION, SINGLE, EP, LIVE)
  - release_year_original (int, optional)
  - release_year_display (int, optional)
  - musicbrainz_release_group_id (string, optional)
  - user_rating (int 1..5, optional)
  - last_listened_at (datetime, optional)
  - library_path (string, optional)
  - created_at, updated_at (datetime)
- Relationships:
  - 1:N with Disc
  - 1:N with Track
- Validation rules:
  - album_type required
  - user_rating between 1 and 5 when present

## Disc
- Fields:
  - id (UUID)
  - album_id (FK Album, required)
  - disc_number (int >= 1)
  - title (string, optional)
- Relationships:
  - 1:N with Track

## Track
- Fields:
  - id (UUID)
  - album_id (FK Album, required)
  - disc_id (FK Disc, optional)
  - artist_id (FK Artist, required)
  - original_artist_id (FK Artist, optional)
  - title (string, required)
  - track_number (int >= 1)
  - duration_seconds (int >= 0)
  - bitrate_kbps (int, optional)
  - sample_rate_hz (int, optional)
  - source_file_path (string, required)
  - organized_file_path (string, optional)
  - extension (string, required)
  - metadata_source (enum: ID3V2, ID3V1, FILE_INFERENCE, MIXED)
  - musicbrainz_recording_id (string, optional)
  - musicbrainz_work_id (string, optional)
  - last_listened_at (datetime, optional)
  - created_at, updated_at (datetime)
- Relationships:
  - N:M with TagLabel via TrackTag
  - 1:N with PlaybackEvent
  - 0..1 with CoverRelation as covered track
- Validation rules:
  - title required
  - track_number required for organized output
  - extension normalized lower-case

## CoverRelation
- Fields:
  - id (UUID)
  - track_id (FK Track, required, unique)
  - original_artist_id (FK Artist, required)
  - source_provider (enum: MUSICBRAINZ, USER_OVERRIDE)
  - confidence (decimal 0..1)
  - created_at
- Validation rules:
  - confidence required for provider=MUSICBRAINZ

## TagLabel
- Fields:
  - id (UUID)
  - name (string, unique, required)
  - color (string, optional)
  - created_at

## TrackTag
- Fields:
  - track_id (FK Track, required)
  - tag_id (FK TagLabel, required)
  - created_at
- Constraints:
  - composite PK (track_id, tag_id)

## PlaybackEvent
- Fields:
  - id (UUID)
  - track_id (FK Track, required)
  - album_id (FK Album, required)
  - started_at (datetime, required)
  - ended_at (datetime, optional)
  - listened_ratio (decimal 0..1)
  - source (enum: PLAYER_UI, GLOBAL_HOTKEY, AUTO_NEXT)
- Validation rules:
  - listened_ratio between 0 and 1

## ImportJob
- Fields:
  - id (UUID)
  - source_root (string, required)
  - destination_root (string, required)
  - status (enum: PENDING, RUNNING, PAUSED, FAILED, COMPLETED)
  - scanned_files (int)
  - processed_files (int)
  - failed_files (int)
  - started_at, finished_at (datetime, optional)
  - checkpoint_token (string, optional)
- State transitions:
  - PENDING -> RUNNING
  - RUNNING -> PAUSED | FAILED | COMPLETED
  - PAUSED -> RUNNING | FAILED

## DuplicateGroup
- Fields:
  - id (UUID)
  - import_job_id (FK ImportJob, required)
  - canonical_hint_title (string, optional)
  - canonical_hint_artist (string, optional)
  - canonical_hint_duration_seconds (int, optional)
  - status (enum: OPEN, RESOLVED, SKIPPED)
  - created_at, resolved_at (datetime, optional)
- Relationships:
  - 1:N with DuplicateCandidate
  - 1:1 with DuplicateResolution (optional while OPEN)
- Validation rules:
  - A group must have at least 2 candidates

## DuplicateCandidate
- Fields:
  - id (UUID)
  - duplicate_group_id (FK DuplicateGroup, required)
  - track_id (FK Track, optional if not persisted yet)
  - source_file_path (string, required)
  - file_size_bytes (long, required)
  - bitrate_kbps (int, optional)
  - sample_rate_hz (int, optional)
  - duration_seconds (int, optional)
  - codec (string, optional)
  - quality_score (decimal 0..1, optional)
- Validation rules:
  - source_file_path must be unique within the same duplicate group

## DuplicateResolution
- Fields:
  - id (UUID)
  - duplicate_group_id (FK DuplicateGroup, required, unique)
  - selected_candidate_id (FK DuplicateCandidate, required)
  - discarded_candidate_ids (UUID[], required)
  - decided_by (enum: USER, AUTO_RULE)
  - decision_reason (string, optional)
  - decided_at (datetime, required)
- Validation rules:
  - selected candidate must belong to duplicate group
  - discarded candidates must belong to duplicate group and exclude selected candidate

## OnlineCatalogCheck
- Fields:
  - id (UUID)
  - artist_id (FK Artist, required)
  - provider (enum: MUSICBRAINZ)
  - checked_at (datetime, required)
  - missing_album_count (int >= 0)
  - status (enum: OK, RATE_LIMITED, FAILED)

## MissingAlbumNotification
- Fields:
  - id (UUID)
  - artist_id (FK Artist, required)
  - album_title (string, required)
  - release_year (int, optional)
  - provider_reference (string, optional)
  - detected_at (datetime)
  - dismissed_at (datetime, optional)
  - acquired_at (datetime, optional)
- Validation rules:
  - dismissed_at and acquired_at are mutually optional; at least one remains null while pending
