package com.polzin.music.infrastructure.filesystem;

import java.nio.file.Path;
import java.util.Objects;

public final class LinkService {

    public enum LinkKind {
        SYMBOLIC,
        DIRECTORY_JUNCTION,
        REGULAR_FILE_REFERENCE
    }

    public record LinkState(Path linkPath, Path targetPath, LinkKind linkKind, String platform) {
    }

    public LinkState createLinkState(Path linkPath, Path targetPath, boolean windowsPlatform) {
        return new LinkState(linkPath, targetPath, windowsPlatform ? LinkKind.DIRECTORY_JUNCTION : LinkKind.SYMBOLIC,
                windowsPlatform ? "WINDOWS" : "LINUX");
    }

    public LinkState regenerateForPlatform(LinkState currentState, boolean windowsPlatform) {
        Objects.requireNonNull(currentState, "currentState");
        LinkKind desiredKind = windowsPlatform ? LinkKind.DIRECTORY_JUNCTION : LinkKind.SYMBOLIC;
        if (currentState.linkKind() == desiredKind && currentState.platform().equals(platformName(windowsPlatform))) {
            return currentState;
        }
        return new LinkState(currentState.linkPath(), currentState.targetPath(), desiredKind, platformName(windowsPlatform));
    }

    private String platformName(boolean windowsPlatform) {
        return windowsPlatform ? "WINDOWS" : "LINUX";
    }
}
