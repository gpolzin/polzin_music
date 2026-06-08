package com.polzin.music.application.player;

import com.polzin.music.infrastructure.player.MediaPlayerService;

import java.nio.file.Path;
import java.util.Objects;

public final class PlaybackController {

    private final PlaybackStateMachine playbackStateMachine;
    private final MediaPlayerService mediaPlayerService;

    public PlaybackController(PlaybackStateMachine playbackStateMachine, MediaPlayerService mediaPlayerService) {
        this.playbackStateMachine = playbackStateMachine;
        this.mediaPlayerService = mediaPlayerService;
    }

    public PlaybackStateMachine.PlaybackSnapshot play(String trackId, Path audioFile) {
        Objects.requireNonNull(audioFile, "audioFile");
        mediaPlayerService.play(audioFile);
        return playbackStateMachine.play(trackId);
    }

    public PlaybackStateMachine.PlaybackSnapshot togglePause() {
        if (playbackStateMachine.state() == PlaybackStateMachine.State.PLAYING) {
            mediaPlayerService.pause();
        }
        return playbackStateMachine.pause();
    }

    public PlaybackStateMachine.PlaybackSnapshot stop() {
        mediaPlayerService.stop();
        return playbackStateMachine.stop();
    }
}
