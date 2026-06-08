package com.polzin.music.application.organizer;

import com.polzin.music.infrastructure.filesystem.LinkService;

import java.nio.file.Path;

public final class CoverLinkService {

    private final LayoutPathBuilder layoutPathBuilder;
    private final LinkService linkService;

    public CoverLinkService(LayoutPathBuilder layoutPathBuilder, LinkService linkService) {
        this.layoutPathBuilder = layoutPathBuilder;
        this.linkService = linkService;
    }

    public LinkService.LinkState buildCoverLink(Path organizedRoot,
                                                String originalArtistName,
                                                String coverTitle,
                                                Path targetPath,
                                                boolean windowsPlatform) {
        Path linkPath = layoutPathBuilder.buildCoverNavigationPath(organizedRoot, originalArtistName, coverTitle);
        return linkService.createLinkState(linkPath, targetPath, windowsPlatform);
    }
}
