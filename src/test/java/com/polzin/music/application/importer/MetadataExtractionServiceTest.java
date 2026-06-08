package com.polzin.music.application.importer;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MetadataExtractionServiceTest {

    @Test
    void delegatesToFallbackParserForExtraction() {
        MetadataExtractionService service = new MetadataExtractionService(new TagFallbackParser());

        var extracted = service.extract(Path.of("C:/music/Album/01 - Intro.mp3"), List.of(), List.of());

        assertTrue(extracted.isPresent());
        assertEquals("01 - Intro", extracted.orElseThrow().title());
    }
}
