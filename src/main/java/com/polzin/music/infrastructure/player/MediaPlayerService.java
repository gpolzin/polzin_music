package com.polzin.music.infrastructure.player;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URI;
import java.nio.file.Path;
import java.util.Objects;

public interface MediaPlayerService {

    void play(Path audioFile);

    void pause();

    void stop();

    boolean isPlaying();

    static MediaPlayerService javaFx() {
        return new JavaFxMediaPlayerService();
    }

    final class JavaFxMediaPlayerService implements MediaPlayerService {

        private MediaPlayer mediaPlayer;

        @Override
        public void play(Path audioFile) {
            Objects.requireNonNull(audioFile, "audioFile");
            stop();
            Media media = new Media(audioFile.toUri().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
        }

        @Override
        public void pause() {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }
        }

        @Override
        public void stop() {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
                mediaPlayer = null;
            }
        }

        @Override
        public boolean isPlaying() {
            return mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
        }
    }
}
