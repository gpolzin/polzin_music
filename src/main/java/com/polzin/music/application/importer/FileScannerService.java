package com.polzin.music.application.importer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class FileScannerService {

    private static final List<String> AUDIO_EXTENSIONS = List.of("mp3", "flac", "m4a", "wma", "wav", "mpc", "opus", "ape", "mp2");

    public List<Path> scan(Path sourceRoot) throws IOException {
        try (var stream = Files.walk(sourceRoot)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(this::isAudioFile)
                    .sorted(Comparator.naturalOrder())
                    .toList();
        }
    }

    private boolean isAudioFile(Path path) {
        String fileName = path.getFileName().toString();
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot < 0 || lastDot == fileName.length() - 1) {
            return false;
        }
        String extension = fileName.substring(lastDot + 1).toLowerCase(Locale.ROOT);
        return AUDIO_EXTENSIONS.contains(extension);
    }
}
