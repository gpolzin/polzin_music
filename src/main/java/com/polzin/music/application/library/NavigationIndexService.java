package com.polzin.music.application.library;

import com.polzin.music.infrastructure.db.repository.LinkRepository;

import java.util.List;

public final class NavigationIndexService {

    private final LinkRepository linkRepository;

    public NavigationIndexService(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    public int countLinks(String rootKey) {
        return linkRepository.findAll(rootKey).size();
    }

    public List<LinkRepository.LinkRecord> loadLinks(String rootKey) {
        return linkRepository.findAll(rootKey);
    }
}
