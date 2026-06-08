package com.polzin.music.application.organizer;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LayoutMappingTest {

    private final LayoutPathBuilder builder = new LayoutPathBuilder();

    @Test
    void buildsCountryStyleAndMarkPathsDeterministically() {
        Path organizedRoot = Path.of("C:/organized");

        assertEquals("C:/organized/Paises/Brasil/Artista Principal", builder.buildCountryLinkPath(organizedRoot, "Brasil", "Artista Principal").toString().replace('\\', '/'));
        assertEquals("C:/organized/Estilos/Rock/Artista Principal", builder.buildStyleLinkPath(organizedRoot, "Rock", "Artista Principal").toString().replace('\\', '/'));
        assertEquals("C:/organized/Marcacoes/Favoritas/Minha Musica", builder.buildMarkLinkPath(organizedRoot, "Favoritas", "Minha Musica").toString().replace('\\', '/'));
    }

    @Test
    void buildsCoverNavigationPathUnderArtistFolder() {
        Path organizedRoot = Path.of("C:/organized");

        assertEquals("C:/organized/Artistas/Artista Principal/Covers/Capa 01", builder.buildCoverNavigationPath(organizedRoot, "Artista Principal", "Capa 01").toString().replace('\\', '/'));
    }
}
