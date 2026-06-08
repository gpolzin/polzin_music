package com.polzin.music.application.organizer;

import com.polzin.music.infrastructure.filesystem.PathSanitizer;

import java.nio.file.Path;

public final class LayoutPathBuilder {

    public Path buildArtistRoot(Path organizedRoot, String artistName) {
        return organizedRoot.resolve("Artistas").resolve(PathSanitizer.sanitizeSegment(artistName));
    }

    public Path buildCountryLinkPath(Path organizedRoot, String countryName, String artistName) {
        return organizedRoot.resolve("Paises")
                .resolve(PathSanitizer.sanitizeSegment(countryName))
                .resolve(PathSanitizer.sanitizeSegment(artistName));
    }

    public Path buildStyleLinkPath(Path organizedRoot, String styleName, String artistName) {
        return organizedRoot.resolve("Estilos")
                .resolve(PathSanitizer.sanitizeSegment(styleName))
                .resolve(PathSanitizer.sanitizeSegment(artistName));
    }

    public Path buildMarkLinkPath(Path organizedRoot, String markName, String musicName) {
        return organizedRoot.resolve("Marcacoes")
                .resolve(PathSanitizer.sanitizeSegment(markName))
                .resolve(PathSanitizer.sanitizeSegment(musicName));
    }

    public Path buildCoverNavigationPath(Path organizedRoot, String originalArtistName, String coverTitle) {
        return organizedRoot.resolve("Artistas")
                .resolve(PathSanitizer.sanitizeSegment(originalArtistName))
                .resolve("Covers")
                .resolve(PathSanitizer.sanitizeSegment(coverTitle));
    }
}
