package com.polzin.music.ui.library;

import com.polzin.music.application.library.CurationService;

import java.util.List;
import java.util.UUID;

public final class CurationController {

    private final CurationService curationService;

    public CurationController(CurationService curationService) {
        this.curationService = curationService;
    }

    public void applyAlbumRating(UUID albumId, int rating) {
        curationService.rateAlbum(albumId, rating);
    }

    public void applyTrackTags(UUID trackId, List<String> tagNames) {
        if (tagNames == null) {
            return;
        }
        for (String tagName : tagNames) {
            curationService.addTag(trackId, tagName);
        }
    }
}
