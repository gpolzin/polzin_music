package com.polzin.music.infrastructure.metadata;

import java.nio.file.Path;
import java.util.Optional;

public interface TagService {

    record Artwork(Path source, String mimeType, byte[] bytes) {
    }

    record TagData(String title,
                   String artist,
                   String album,
                   Integer trackNumber,
                   Integer year,
                   Optional<Artwork> artwork) {
    }

    Optional<TagData> read(Path audioFile);

    void write(Path audioFile, TagData tagData);
}
