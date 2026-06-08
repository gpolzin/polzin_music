ALTER TABLE artists ADD COLUMN source_provider TEXT;
ALTER TABLE artists ADD COLUMN source_reference TEXT;
ALTER TABLE artists ADD COLUMN resolved_at TEXT;

ALTER TABLE albums ADD COLUMN source_provider TEXT;
ALTER TABLE albums ADD COLUMN source_reference TEXT;
ALTER TABLE albums ADD COLUMN resolved_at TEXT;

ALTER TABLE tracks ADD COLUMN source_provider TEXT;
ALTER TABLE tracks ADD COLUMN source_reference TEXT;
ALTER TABLE tracks ADD COLUMN resolved_at TEXT;

ALTER TABLE duplicate_candidates ADD COLUMN source_provider TEXT;
ALTER TABLE duplicate_candidates ADD COLUMN source_reference TEXT;

ALTER TABLE online_catalog_checks ADD COLUMN source_reference TEXT;
