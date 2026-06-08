package com.polzin.music.ui.player;

import com.polzin.music.application.player.PlaybackController;
import com.polzin.music.application.player.PlaybackStateMachine;

import java.nio.file.Path;

public final class PlayerController {

    private final PlaybackController playbackController;

    public PlayerController(PlaybackController playbackController) {
        this.playbackController = playbackController;
    }

    public PlaybackStateMachine.PlaybackSnapshot play(String trackId, Path audioFile) {
        return playbackController.play(trackId, audioFile);
    }

    public PlaybackStateMachine.PlaybackSnapshot pauseOrResume() {
        return playbackController.togglePause();
    }

    public PlaybackStateMachine.PlaybackSnapshot stop() {
        return playbackController.stop();
    }
}
