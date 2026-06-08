# Contract - Organized Filesystem Layout

## Purpose
Define deterministic output paths and naming for organized media and navigation links.

## Metadata Identity Contract
- Canonical identity for artists/albums/tracks must come from MusicBrainz when available.
- Complementary provider IDs (Discogs, Wikidata, AcoustID) must be stored in the local database,
  but MUST NOT alter deterministic folder naming once canonical identity is established.
- Any user-approved override must preserve prior provider references for traceability.

## Root Structure
- <organized>/Artistas/<artist_name>/<album_type>/<year - album_name>/
- <organized>/Paises/<country_name>/<artist_link>
- <organized>/Estilos/<style_name>/<artist_link>
- <organized>/Marcacoes/<nome_marcacao>/<music_link>

## Album Type Mapping
- STUDIO -> Estudio
- COMPILATION -> Compilacao
- SINGLE -> Single
- EP -> EP
- LIVE -> Ao Vivo

## Track Naming
- Format: NN - <track_title>.<extension>
- NN must be two digits, zero-padded (01..99)

## Sanitization Rules
- Remove/replace invalid filesystem characters by OS policy.
- Trim trailing spaces and dots where required by platform.
- Preserve human-readable accents in display names when platform supports them.

## Linking Rules
- Country links:
  - source: <organized>/Paises/<country_name>/<artist_name>
  - target: artist root folder
- Style links:
  - source: <organized>/Estilos/<style_name>/<artist_name>
  - target: artist root folder
- Mark links:
  - source: <organized>/Marcacoes/<nome_marcacao>/<music_name>
  - target: music track location
- Cover links:
  - source: under original artist cover navigation folder
  - target: cover track or album location

## Provider-Aware Link Resolution
- Country and artist aliases may be enriched from Wikidata, but displayed path names follow
  the normalized canonical artist name.
- Style names may be enriched and normalized from all available metadata providers, but the
  displayed path name must remain stable once canonicalized.
- Cover relationship links prefer MusicBrainz relationship data.
- If provider conflict exists for cover relation, keep canonical link target and record conflict
  in DB for review without generating duplicate contradictory links.

## Cross-OS Compatibility
- Windows and Linux must both be supported.
- Link state must be tracked to allow regeneration when moving between OS semantics.

## Conflict Resolution
- If target file exists with different checksum, append deterministic suffix.
- If metadata changes path identity, create move plan and preserve history in DB.
- If cross-provider metadata differs (title/year/type), keep canonical path and save alternate
  values as aliases in DB to avoid path churn.
