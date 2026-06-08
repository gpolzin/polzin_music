package com.polzin.music.infrastructure.filesystem;

import java.text.Normalizer;

public final class PathSanitizer {

    private PathSanitizer() {
    }

    public static String sanitizeSegment(String value) {
        if (value == null || value.isBlank()) {
            return "unknown";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFKC)
                .replaceAll("[\\\\/:*?\"<>|\\u0000]", " ")
                .replaceAll("\\s+", " ")
                .trim();
        normalized = normalized.replaceAll("[. ]+$", "");
        return normalized.isBlank() ? "unknown" : normalized;
    }

    public static String formatTrackFileName(int trackNumber, String title, String extension) {
        String sanitizedTitle = sanitizeSegment(title);
        String sanitizedExtension = sanitizeSegment(extension).toLowerCase();
        return String.format("%02d - %s.%s", trackNumber, sanitizedTitle, sanitizedExtension);
    }

    public static String formatAlbumFolderName(int releaseYear, String albumTitle) {
        return String.format("%d - %s", releaseYear, sanitizeSegment(albumTitle));
    }
}
