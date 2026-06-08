package com.polzin.music.application.player;

import java.util.Objects;
import java.util.Optional;

public final class PlaybackStateMachine {

    public enum State {
        STOPPED,
        PLAYING,
        PAUSED
    }

    public record PlaybackSnapshot(State state, String currentTrackId) {
    }

    private State state = State.STOPPED;
    private String currentTrackId;

    public PlaybackSnapshot play(String trackId) {
        currentTrackId = Objects.requireNonNull(trackId, "trackId");
        state = State.PLAYING;
        return snapshot();
    }

    public PlaybackSnapshot pause() {
        if (state == State.PLAYING) {
            state = State.PAUSED;
        }
        return snapshot();
    }

    public PlaybackSnapshot stop() {
        state = State.STOPPED;
        currentTrackId = null;
        return snapshot();
    }

    public Optional<String> currentTrackId() {
        return Optional.ofNullable(currentTrackId);
    }

    public State state() {
        return state;
    }

    public PlaybackSnapshot snapshot() {
        return new PlaybackSnapshot(state, currentTrackId);
    }
}
