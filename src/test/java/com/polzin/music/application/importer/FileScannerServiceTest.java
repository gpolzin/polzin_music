package com.polzin.music.application.importer;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileScannerServiceTest {

    @Test
    void scansRecursivelyAndReturnsOnlySupportedAudioFiles() throws Exception {
        Path root = Files.createTempDirectory("polzin-scan");
        try {
            Files.createDirectories(root.resolve("Album"));
            Files.writeString(root.resolve("Album/01 - Intro.mp3"), "x");
            Files.writeString(root.resolve("Album/02 - Bonus.txt"), "x");
            Files.writeString(root.resolve("Album/03 - Live.FLAC"), "x");

            FileScannerService service = new FileScannerService();

            assertEquals(2, service.scan(root).size());
        } finally {
            try (var stream = Files.walk(root)) {
                stream.sorted((left, right) -> right.compareTo(left)).forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (Exception ignored) {
                    }
                });
            }
        }
    }
}
