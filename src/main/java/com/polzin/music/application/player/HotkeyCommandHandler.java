package com.polzin.music.application.player;

import com.polzin.music.infrastructure.player.GlobalHotkeyService;

public final class HotkeyCommandHandler {

    private final GlobalHotkeyService globalHotkeyService;
    private final PlaybackController playbackController;

    public HotkeyCommandHandler(GlobalHotkeyService globalHotkeyService, PlaybackController playbackController) {
        this.globalHotkeyService = globalHotkeyService;
        this.playbackController = playbackController;
    }

    public void registerPlayPauseHotkey() {
        globalHotkeyService.registerPlayPause(playbackController::togglePause);
    }

    public void unregisterAll() {
        globalHotkeyService.unregisterAll();
    }
}
