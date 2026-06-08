package com.polzin.music.integration.filesystem;

import com.polzin.music.infrastructure.filesystem.LinkService;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CrossOsLinkRegenerationIT {

    @Test
    void regeneratesLinkStateWhenPlatformChanges() {
        LinkService linkService = new LinkService();
        LinkService.LinkState windowsState = linkService.createLinkState(
                Path.of("C:/organized/Paises/Brasil/Artista"),
                Path.of("C:/organized/Artistas/Artista"),
                true
        );

        LinkService.LinkState linuxState = linkService.regenerateForPlatform(windowsState, false);

        assertEquals("WINDOWS", windowsState.platform());
        assertEquals("LINUX", linuxState.platform());
        assertEquals(windowsState.linkPath(), linuxState.linkPath());
        assertEquals(windowsState.targetPath(), linuxState.targetPath());
    }
}
