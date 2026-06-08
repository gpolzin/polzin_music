CREATE TABLE IF NOT EXISTS artists (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    musicbrainz_id TEXT UNIQUE,
    country_code TEXT,
    aliases TEXT,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS albums (
    id TEXT PRIMARY KEY,
    artist_id TEXT NOT NULL,
    title TEXT NOT NULL,
    album_type TEXT NOT NULL,
    release_year_original INTEGER,
    release_year_display INTEGER,
    musicbrainz_release_group_id TEXT,
    user_rating INTEGER,
    last_listened_at TEXT,
    library_path TEXT,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (artist_id) REFERENCES artists(id)
);

CREATE TABLE IF NOT EXISTS discs (
    id TEXT PRIMARY KEY,
    album_id TEXT NOT NULL,
    disc_number INTEGER NOT NULL,
    title TEXT,
    FOREIGN KEY (album_id) REFERENCES albums(id)
);

CREATE TABLE IF NOT EXISTS tracks (
    id TEXT PRIMARY KEY,
    album_id TEXT NOT NULL,
    disc_id TEXT,
    artist_id TEXT NOT NULL,
    original_artist_id TEXT,
    title TEXT NOT NULL,
    track_number INTEGER NOT NULL,
    duration_seconds INTEGER NOT NULL DEFAULT 0,
    bitrate_kbps INTEGER,
    sample_rate_hz INTEGER,
    source_file_path TEXT NOT NULL,
    organized_file_path TEXT,
    extension TEXT NOT NULL,
    metadata_source TEXT NOT NULL,
    musicbrainz_recording_id TEXT,
    musicbrainz_work_id TEXT,
    last_listened_at TEXT,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (album_id) REFERENCES albums(id),
    FOREIGN KEY (disc_id) REFERENCES discs(id),
    FOREIGN KEY (artist_id) REFERENCES artists(id),
    FOREIGN KEY (original_artist_id) REFERENCES artists(id)
);

CREATE TABLE IF NOT EXISTS tag_labels (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    color TEXT,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS track_tags (
    track_id TEXT NOT NULL,
    tag_id TEXT NOT NULL,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (track_id, tag_id),
    FOREIGN KEY (track_id) REFERENCES tracks(id),
    FOREIGN KEY (tag_id) REFERENCES tag_labels(id)
);

CREATE TABLE IF NOT EXISTS playback_events (
    id TEXT PRIMARY KEY,
    track_id TEXT NOT NULL,
    album_id TEXT NOT NULL,
    started_at TEXT NOT NULL,
    ended_at TEXT,
    listened_ratio REAL NOT NULL,
    source TEXT NOT NULL,
    FOREIGN KEY (track_id) REFERENCES tracks(id),
    FOREIGN KEY (album_id) REFERENCES albums(id)
);

CREATE TABLE IF NOT EXISTS import_jobs (
    id TEXT PRIMARY KEY,
    source_root TEXT NOT NULL,
    destination_root TEXT NOT NULL,
    status TEXT NOT NULL,
    scanned_files INTEGER NOT NULL DEFAULT 0,
    processed_files INTEGER NOT NULL DEFAULT 0,
    failed_files INTEGER NOT NULL DEFAULT 0,
    started_at TEXT,
    finished_at TEXT,
    checkpoint_token TEXT
);

CREATE TABLE IF NOT EXISTS duplicate_groups (
    id TEXT PRIMARY KEY,
    import_job_id TEXT NOT NULL,
    canonical_hint_title TEXT,
    canonical_hint_artist TEXT,
    canonical_hint_duration_seconds INTEGER,
    status TEXT NOT NULL,
    created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at TEXT,
    FOREIGN KEY (import_job_id) REFERENCES import_jobs(id)
);

CREATE TABLE IF NOT EXISTS duplicate_candidates (
    id TEXT PRIMARY KEY,
    duplicate_group_id TEXT NOT NULL,
    track_id TEXT,
    source_file_path TEXT NOT NULL,
    file_size_bytes INTEGER NOT NULL,
    bitrate_kbps INTEGER,
    sample_rate_hz INTEGER,
    duration_seconds INTEGER,
    codec TEXT,
    quality_score REAL,
    FOREIGN KEY (duplicate_group_id) REFERENCES duplicate_groups(id),
    FOREIGN KEY (track_id) REFERENCES tracks(id)
);

CREATE TABLE IF NOT EXISTS duplicate_resolutions (
    id TEXT PRIMARY KEY,
    duplicate_group_id TEXT NOT NULL UNIQUE,
    selected_candidate_id TEXT NOT NULL,
    discarded_candidate_ids TEXT NOT NULL,
    decided_by TEXT NOT NULL,
    decision_reason TEXT,
    decided_at TEXT NOT NULL,
    FOREIGN KEY (duplicate_group_id) REFERENCES duplicate_groups(id)
);

CREATE TABLE IF NOT EXISTS online_catalog_checks (
    id TEXT PRIMARY KEY,
    artist_id TEXT NOT NULL,
    provider TEXT NOT NULL,
    checked_at TEXT NOT NULL,
    missing_album_count INTEGER NOT NULL DEFAULT 0,
    status TEXT NOT NULL,
    FOREIGN KEY (artist_id) REFERENCES artists(id)
);

CREATE TABLE IF NOT EXISTS missing_album_notifications (
    id TEXT PRIMARY KEY,
    artist_id TEXT NOT NULL,
    album_title TEXT NOT NULL,
    release_year INTEGER,
    provider_reference TEXT,
    detected_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
    dismissed_at TEXT,
    acquired_at TEXT,
    FOREIGN KEY (artist_id) REFERENCES artists(id)
);
