package com.polzin.music.contract.filesystem;

import com.polzin.music.application.organizer.LayoutPathBuilder;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LinkLayoutContractTest {

    @Test
    void countryStyleAndMarkLinkTargetsFollowCanonicalLayout() {
        LayoutPathBuilder builder = new LayoutPathBuilder();
        Path organizedRoot = Path.of("/organized");

        assertEquals("/organized/Paises/Brasil/Artista X", builder.buildCountryLinkPath(organizedRoot, "Brasil", "Artista X").toString().replace('\\', '/'));
        assertEquals("/organized/Estilos/Samba/Artista X", builder.buildStyleLinkPath(organizedRoot, "Samba", "Artista X").toString().replace('\\', '/'));
        assertEquals("/organized/Marcacoes/Preferidas/Faixa 01", builder.buildMarkLinkPath(organizedRoot, "Preferidas", "Faixa 01").toString().replace('\\', '/'));
    }
}
