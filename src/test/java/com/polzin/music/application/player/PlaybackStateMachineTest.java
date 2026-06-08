package com.polzin.music.application.player;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlaybackStateMachineTest {

    @Test
    void transitionsFromStoppedToPlayingToPausedAndBackToStopped() {
        PlaybackStateMachine stateMachine = new PlaybackStateMachine();

        assertEquals(PlaybackStateMachine.State.STOPPED, stateMachine.state());

        assertEquals(PlaybackStateMachine.State.PLAYING, stateMachine.play("track-1").state());
        assertTrue(stateMachine.currentTrackId().isPresent());

        assertEquals(PlaybackStateMachine.State.PAUSED, stateMachine.pause().state());
        assertEquals(PlaybackStateMachine.State.STOPPED, stateMachine.stop().state());
        assertTrue(stateMachine.currentTrackId().isEmpty());
    }
}
