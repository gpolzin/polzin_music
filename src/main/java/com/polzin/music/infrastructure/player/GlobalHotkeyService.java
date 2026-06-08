package com.polzin.music.infrastructure.player;

public interface GlobalHotkeyService {

    void registerPlayPause(Runnable action);

    void unregisterAll();
}
