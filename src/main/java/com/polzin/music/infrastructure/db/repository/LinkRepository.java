package com.polzin.music.infrastructure.db.repository;

import com.polzin.music.infrastructure.filesystem.LinkService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class LinkRepository {

    public record LinkRecord(LinkService.LinkState linkState, String sourceProvider, String sourceReference) {
    }

    private final Map<String, List<LinkRecord>> recordsByRoot = new ConcurrentHashMap<>();

    public void save(String rootKey, LinkRecord record) {
        recordsByRoot.computeIfAbsent(rootKey, key -> new ArrayList<>()).add(record);
    }

    public List<LinkRecord> findAll(String rootKey) {
        return Collections.unmodifiableList(recordsByRoot.getOrDefault(rootKey, List.of()));
    }
}
