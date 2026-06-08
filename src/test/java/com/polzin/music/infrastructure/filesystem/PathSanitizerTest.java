package com.polzin.music.infrastructure.filesystem;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PathSanitizerTest {

    @Test
    void sanitizesInvalidFilesystemCharacters() {
        assertEquals("AC DC live", PathSanitizer.sanitizeSegment("AC/DC: live?"));
    }

    @Test
    void formatsTrackFileNameWithZeroPadding() {
        assertEquals("03 - Minha Música.mp3", PathSanitizer.formatTrackFileName(3, "Minha Música", "MP3"));
    }
}
