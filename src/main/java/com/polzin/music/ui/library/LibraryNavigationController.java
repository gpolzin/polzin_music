package com.polzin.music.ui.library;

import com.polzin.music.application.library.NavigationIndexService;

public final class LibraryNavigationController {

    private final NavigationIndexService navigationIndexService;
    private int refreshCount;

    public LibraryNavigationController(NavigationIndexService navigationIndexService) {
        this.navigationIndexService = navigationIndexService;
    }

    public void refresh(String rootKey) {
        navigationIndexService.countLinks(rootKey);
        refreshCount++;
    }

    public int refreshCount() {
        return refreshCount;
    }
}
