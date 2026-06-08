# Contract - Player Controls, Listening History, and Recommendations

## Purpose
Define player control events, listening history persistence, and recommendation scoring behavior.

## Player Control Input Contract
- command: enum { PLAY, PAUSE, NEXT, PREVIOUS, SEEK }
- source: enum { UI, GLOBAL_HOTKEY }
- timestamp: datetime
- context_track_id: UUID|null

## Listening Event Contract
- track_id: UUID (required)
- album_id: UUID (required)
- started_at: datetime (required)
- ended_at: datetime (optional)
- listened_ratio: decimal (0..1) (required for completed event)

## Global Key Contract
- Minimum required mapping:
  - Play/Pause
- Optional mappings:
  - Next/Previous
- Event handling SLA:
  - response <= 200ms for mapped keys during active playback

## Recommendation Input Contract
- candidate_albums: array<album_id>
- album_rating: int (1..5)|null
- last_listened_at: datetime|null
- recent_play_count_window_30d: int
- metadata_confidence: decimal (0..1)
- metadata_providers: array<enum { MUSICBRAINZ, DISCOGS, WIKIDATA, ACOUSTID, COVER_ART_ARCHIVE }>

## Recommendation Scoring Contract
- score = (w_rating * normalized_rating)
        + (w_recency * normalized_days_since_last_listen)
        - (w_repeat * normalized_recent_play_count)
- Weights configurable with safe defaults.

## Recommendation Output Contract
- recommendation_items: array
  - album_id: UUID
  - score: decimal
  - reason_codes: string[] (e.g., HIGH_RATING, LONG_TIME_UNHEARD, LOW_REPEAT)
  - metadata_confidence: decimal (0..1)
  - provenance_summary: string[]

## Acceptance Notes
- Must reduce 30-day repetition by >= 40% against chronological baseline (SC-009).
- Must persist last-listened values for album and track (FR-015).
- Recommendation should not downrank an album solely for missing complementary provider data
  when canonical metadata is valid.
