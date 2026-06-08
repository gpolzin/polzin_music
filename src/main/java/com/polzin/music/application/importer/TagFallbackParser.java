package com.polzin.music.application.importer;

import java.util.List;
import java.util.Optional;

public final class TagFallbackParser {

    public record ParsedTag(String title, String artist, String album, Integer trackNumber, Integer year) {
    }

    public Optional<ParsedTag> parse(List<String> id3v2Frames, List<String> id3v1Frames, String fileName, String parentFolder) {
        Optional<ParsedTag> v2 = parseFrames(id3v2Frames);
        if (v2.isPresent()) {
            return v2;
        }
        Optional<ParsedTag> v1 = parseFrames(id3v1Frames);
        if (v1.isPresent()) {
            return v1;
        }
        return inferFromFileSystem(fileName, parentFolder);
    }

    private Optional<ParsedTag> parseFrames(List<String> frames) {
        if (frames == null) {
            return Optional.empty();
        }
        String title = null;
        String artist = null;
        String album = null;
        Integer trackNumber = null;
        Integer year = null;
        for (String frame : frames) {
            if (frame == null || !frame.contains("=")) {
                continue;
            }
            String[] parts = frame.split("=", 2);
            switch (parts[0].trim().toUpperCase()) {
                case "TITLE" -> title = parts[1].trim();
                case "ARTIST" -> artist = parts[1].trim();
                case "ALBUM" -> album = parts[1].trim();
                case "TRACK" -> trackNumber = parseInteger(parts[1].trim());
                case "YEAR" -> year = parseInteger(parts[1].trim());
                default -> {
                }
            }
        }
        if (title == null && artist == null && album == null && trackNumber == null && year == null) {
            return Optional.empty();
        }
        return Optional.of(new ParsedTag(title, artist, album, trackNumber, year));
    }

    private Optional<ParsedTag> inferFromFileSystem(String fileName, String parentFolder) {
        if ((fileName == null || fileName.isBlank()) && (parentFolder == null || parentFolder.isBlank())) {
            return Optional.empty();
        }
        return Optional.of(new ParsedTag(
                stripExtension(fileName),
                parentFolder,
                null,
                null,
                null
        ));
    }

    private Integer parseInteger(String value) {
        try {
            return Integer.valueOf(value.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private String stripExtension(String value) {
        if (value == null) {
            return null;
        }
        int lastDot = value.lastIndexOf('.');
        return lastDot > 0 ? value.substring(0, lastDot) : value;
    }
}
