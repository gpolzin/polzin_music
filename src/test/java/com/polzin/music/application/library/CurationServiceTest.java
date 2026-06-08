package com.polzin.music.application.library;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CurationServiceTest {

    @Test
    void storesAlbumRatingsAndTrackTags() {
        CurationService service = new CurationService();
        UUID albumId = UUID.fromString("77777777-7777-7777-7777-777777777777");
        UUID trackId = UUID.fromString("88888888-8888-8888-8888-888888888888");

        service.rateAlbum(albumId, 5);
        service.addTag(trackId, "Favoritas");

        assertEquals(5, service.albumRatings().get(albumId));
        assertEquals("Favoritas", service.tagsForTrack(trackId).getFirst());
    }

    @Test
    void rejectsInvalidAlbumRating() {
        CurationService service = new CurationService();

        assertThrows(IllegalArgumentException.class, () -> service.rateAlbum(UUID.randomUUID(), 0));
    }
}
