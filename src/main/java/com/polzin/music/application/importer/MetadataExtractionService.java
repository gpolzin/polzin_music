package com.polzin.music.application.importer;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public final class MetadataExtractionService {

    private final TagFallbackParser tagFallbackParser;

    public MetadataExtractionService(TagFallbackParser tagFallbackParser) {
        this.tagFallbackParser = tagFallbackParser;
    }

    public Optional<TagFallbackParser.ParsedTag> extract(Path audioFile, List<String> id3v2Frames, List<String> id3v1Frames) {
        Path parentFolder = audioFile.getParent();
        return tagFallbackParser.parse(
                id3v2Frames,
                id3v1Frames,
                audioFile.getFileName().toString(),
                parentFolder == null ? null : parentFolder.getFileName().toString()
        );
    }
}
