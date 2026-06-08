package com.polzin.music.application.importer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TagFallbackParserTest {

    private final TagFallbackParser parser = new TagFallbackParser();

    @Test
    void prefersId3v2OverFallbacks() {
        var parsed = parser.parse(
                List.of("TITLE=Faixa principal", "ARTIST=Artista", "TRACK=7"),
                List.of("TITLE=Faixa antiga"),
                "faixa.mp3",
                "Album"
        );

        assertTrue(parsed.isPresent());
        assertEquals("Faixa principal", parsed.orElseThrow().title());
        assertEquals(7, parsed.orElseThrow().trackNumber());
    }

    @Test
    void fallsBackToFileSystemInferenceWhenTagsAreMissing() {
        var parsed = parser.parse(List.of(), List.of(), "01 - Introducao.mp3", "Meu Album");

        assertTrue(parsed.isPresent());
        assertEquals("01 - Introducao", parsed.orElseThrow().title());
        assertEquals("Meu Album", parsed.orElseThrow().artist());
    }
}
