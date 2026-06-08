package com.polzin.music.infrastructure.metadata;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;

public interface TagWriteService {

    void write(Path audioFile, TagService.TagData tagData);

    static TagWriteService defaultWriter() {
        return new JAudioTaggerTagWriteService();
    }

    final class JAudioTaggerTagWriteService implements TagWriteService {

        @Override
        public void write(Path audioFile, TagService.TagData tagData) {
            try {
                AudioFile taggedAudioFile = AudioFileIO.read(audioFile.toFile());
                Tag tag = taggedAudioFile.getTagOrCreateAndSetDefault();
                writeText(tag, FieldKey.TITLE, tagData.title());
                writeText(tag, FieldKey.ARTIST, tagData.artist());
                writeText(tag, FieldKey.ALBUM, tagData.album());
                writeText(tag, FieldKey.TRACK, tagData.trackNumber() == null ? null : String.valueOf(tagData.trackNumber()));
                writeText(tag, FieldKey.YEAR, tagData.year() == null ? null : String.valueOf(tagData.year()));
                tag.deleteArtworkField();
                tagData.artwork().ifPresent(artwork -> {
                    try {
                        Artwork embeddedArtwork = Artwork.createArtworkFromFile(writeArtworkToTempFile(artwork));
                        tag.setField(embeddedArtwork);
                    } catch (Exception exception) {
                        throw new IllegalStateException("Failed to prepare embedded artwork", exception);
                    }
                });
                taggedAudioFile.commit();
            } catch (Exception exception) {
                throw new IllegalStateException("Failed to write tags to " + audioFile, exception);
            }
        }

        private void writeText(Tag tag, FieldKey fieldKey, String value) throws Exception {
            if (value != null && !value.isBlank()) {
                tag.setField(fieldKey, value);
            }
        }

        private File writeArtworkToTempFile(TagService.Artwork artwork) throws IOException {
            File tempFile = Files.createTempFile("polzin-artwork", ".img").toFile();
            Files.write(tempFile.toPath(), artwork.bytes());
            tempFile.deleteOnExit();
            return tempFile;
        }
    }
}
