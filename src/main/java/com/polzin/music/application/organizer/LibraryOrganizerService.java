package com.polzin.music.application.organizer;

import com.polzin.music.infrastructure.filesystem.PathSanitizer;

import java.nio.file.Path;

public final class LibraryOrganizerService {

    public enum AlbumType {
        STUDIO,
        COMPILATION,
        SINGLE,
        EP,
        LIVE
    }

    public Path buildAlbumDirectory(Path organizedRoot, String artistName, AlbumType albumType, int releaseYear, String albumTitle) {
        return organizedRoot
                .resolve("Artistas")
                .resolve(PathSanitizer.sanitizeSegment(artistName))
                .resolve(mapAlbumType(albumType))
                .resolve(PathSanitizer.formatAlbumFolderName(releaseYear, albumTitle));
    }

    public Path buildTrackPath(Path albumDirectory, int trackNumber, String trackTitle, String extension) {
        return albumDirectory.resolve(PathSanitizer.formatTrackFileName(trackNumber, trackTitle, extension));
    }

    private String mapAlbumType(AlbumType albumType) {
        return switch (albumType) {
            case STUDIO -> "Estudio";
            case COMPILATION -> "Compilacao";
            case SINGLE -> "Single";
            case EP -> "EP";
            case LIVE -> "Ao Vivo";
        };
    }
}
