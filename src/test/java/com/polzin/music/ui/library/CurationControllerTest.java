package com.polzin.music.ui.library;

import com.polzin.music.application.library.CurationService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CurationControllerTest {

    @Test
    void appliesAlbumRatingAndTrackTags() {
        CurationService curationService = new CurationService();
        CurationController controller = new CurationController(curationService);

        UUID albumId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        UUID trackId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");

        controller.applyAlbumRating(albumId, 4);
        controller.applyTrackTags(trackId, List.of("Favoritas", "Jazz"));

        assertEquals(4, curationService.albumRatings().get(albumId));
        assertEquals(2, curationService.tagsForTrack(trackId).size());
    }
}
