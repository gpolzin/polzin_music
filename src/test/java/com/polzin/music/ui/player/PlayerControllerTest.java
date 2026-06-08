package com.polzin.music.ui.player;

import com.polzin.music.application.player.PlaybackController;
import com.polzin.music.application.player.PlaybackStateMachine;
import com.polzin.music.infrastructure.player.MediaPlayerService;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerControllerTest {

    @Test
    void delegatesPlaybackActionsToApplicationController() {
        PlaybackStateMachine stateMachine = new PlaybackStateMachine();
        PlaybackController playbackController = new PlaybackController(stateMachine, new MediaPlayerService() {
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

        PlayerController playerController = new PlayerController(playbackController);

        assertEquals(PlaybackStateMachine.State.PLAYING, playerController.play("track-1", Path.of("track.mp3")).state());
        assertEquals(PlaybackStateMachine.State.PAUSED, playerController.pauseOrResume().state());
        assertEquals(PlaybackStateMachine.State.STOPPED, playerController.stop().state());
    }
}
