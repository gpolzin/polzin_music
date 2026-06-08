package com.polzin.music.contract.player;

import com.polzin.music.application.player.HotkeyCommandHandler;
import com.polzin.music.application.player.PlaybackController;
import com.polzin.music.application.player.PlaybackStateMachine;
import com.polzin.music.infrastructure.player.GlobalHotkeyService;
import com.polzin.music.infrastructure.player.MediaPlayerService;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalHotkeyContractTest {

    @Test
    void playPauseHotkeyBridgesToPlaybackController() {
        AtomicInteger callbackCount = new AtomicInteger();
        GlobalHotkeyService globalHotkeyService = new GlobalHotkeyService() {
            @Override
            public void registerPlayPause(Runnable action) {
                callbackCount.incrementAndGet();
                action.run();
            }

            @Override
            public void unregisterAll() {
            }
        };

        PlaybackController playbackController = new PlaybackController(new PlaybackStateMachine(), new MediaPlayerService() {
            @Override
            public void play(Path audioFile) {
            }

            @Override
            public void pause() {
            }

            @Override
            public void stop() {
            }

            @Override
            public boolean isPlaying() {
                return false;
            }
        });

        HotkeyCommandHandler handler = new HotkeyCommandHandler(globalHotkeyService, playbackController);
        handler.registerPlayPauseHotkey();

        assertEquals(1, callbackCount.get());
    }
}
