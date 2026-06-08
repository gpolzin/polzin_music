package com.polzin.music.integration.metadata;

import com.polzin.music.infrastructure.metadata.TagService;
import com.polzin.music.infrastructure.metadata.TagWriteService;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmbeddedArtworkIT {

    @Test
    void passesArtworkBytesToTagWriter() {
        AtomicReference<TagService.TagData> captured = new AtomicReference<>();
        TagWriteService writer = (audioFile, tagData) -> captured.set(tagData);

        TagService.TagData tagData = new TagService.TagData(
                "Faixa",
                "Artista",
                "Album",
                1,
                2026,
                Optional.of(new TagService.Artwork(Path.of("cover.jpg"), "image/jpeg", new byte[] {1, 2, 3}))
        );

        writer.write(Path.of("track.mp3"), tagData);

        assertTrue(captured.get().artwork().isPresent());
        assertEquals(3, captured.get().artwork().orElseThrow().bytes().length);
    }
}
