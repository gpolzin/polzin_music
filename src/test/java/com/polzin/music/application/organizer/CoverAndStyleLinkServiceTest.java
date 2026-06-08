package com.polzin.music.application.organizer;

import com.polzin.music.infrastructure.filesystem.LinkService;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CoverAndStyleLinkServiceTest {

    @Test
    void buildsCoverStyleAndMarkLinkStates() {
        LayoutPathBuilder layoutPathBuilder = new LayoutPathBuilder();
        LinkService linkService = new LinkService();
        CoverLinkService coverLinkService = new CoverLinkService(layoutPathBuilder, linkService);
        StyleAndMarkLinkService styleAndMarkLinkService = new StyleAndMarkLinkService(layoutPathBuilder, linkService);

        LinkService.LinkState coverLink = coverLinkService.buildCoverLink(
                Path.of("C:/organized"),
                "Artista Original",
                "Capa Principal",
                Path.of("C:/organized/Artistas/Artista Original/Covers/Capa Principal"),
                true
        );

        LinkService.LinkState styleLink = styleAndMarkLinkService.buildStyleLink(
                Path.of("C:/organized"),
                "Rock",
                "Artista Original",
                Path.of("C:/organized/Artistas/Artista Original"),
                false
        );

        List<LinkService.LinkState> markLinks = styleAndMarkLinkService.buildMarkLinks(
                Path.of("C:/organized"),
                List.of("Favoritas"),
                "Faixa 01",
                Path.of("C:/organized/Artistas/Artista Original/Ao Vivo/2026 - Show/01 - Faixa 01.mp3"),
                false
        );

        assertEquals("C:/organized/Artistas/Artista Original/Covers/Capa Principal", coverLink.linkPath().toString().replace('\\', '/'));
        assertEquals("C:/organized/Estilos/Rock/Artista Original", styleLink.linkPath().toString().replace('\\', '/'));
        assertEquals(1, markLinks.size());
    }
}
