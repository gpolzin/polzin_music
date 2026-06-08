package com.polzin.music.application.organizer;

import com.polzin.music.infrastructure.filesystem.LinkService;

import java.nio.file.Path;
import java.util.List;

public final class StyleAndMarkLinkService {

    private final LayoutPathBuilder layoutPathBuilder;
    private final LinkService linkService;

    public StyleAndMarkLinkService(LayoutPathBuilder layoutPathBuilder, LinkService linkService) {
        this.layoutPathBuilder = layoutPathBuilder;
        this.linkService = linkService;
    }

    public LinkService.LinkState buildStyleLink(Path organizedRoot,
                                                String styleName,
                                                String artistName,
                                                Path artistRoot,
                                                boolean windowsPlatform) {
        return linkService.createLinkState(layoutPathBuilder.buildStyleLinkPath(organizedRoot, styleName, artistName), artistRoot, windowsPlatform);
    }

    public List<LinkService.LinkState> buildMarkLinks(Path organizedRoot,
                                                      List<String> markNames,
                                                      String trackName,
                                                      Path trackPath,
                                                      boolean windowsPlatform) {
        if (markNames == null || markNames.isEmpty()) {
            return List.of();
        }
        return markNames.stream()
                .map(markName -> linkService.createLinkState(
                        layoutPathBuilder.buildMarkLinkPath(organizedRoot, markName, trackName),
                        trackPath,
                        windowsPlatform))
                .toList();
    }
}
