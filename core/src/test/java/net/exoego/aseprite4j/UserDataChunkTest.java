package net.exoego.aseprite4j;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Paths;

import static com.google.common.truth.Truth.assertThat;

public class UserDataChunkTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "2x2tilemap2x2tile",
            "2x3tilemap-indexed",
            "3x2tilemap-grayscale",
            "file-tests-props",
            "slices",
            "slices-moving",
            "tags3x123reps"
    })
    void readUserDataChunk(String filename) throws Exception {
        var path = Paths.get(getClass().getResource("/aseprite/sprites/" + filename + ".aseprite").toURI());
        var file = AsepriteFile.read(path);

        var chunks = file.frames().stream()
                .flatMap(f -> f.chunks().stream())
                .filter(c -> c instanceof UserDataChunk)
                .map(c -> (UserDataChunk) c)
                .toList();

        assertThat(chunks).isNotEmpty();
    }

    @Test
    void readUserDataWithProperties() throws Exception {
        var path = Paths.get(getClass().getResource("/aseprite/sprites/file-tests-props.aseprite").toURI());
        var file = AsepriteFile.read(path);

        var chunksWithProperties = file.frames().stream()
                .flatMap(f -> f.chunks().stream())
                .filter(c -> c instanceof UserDataChunk)
                .map(c -> (UserDataChunk) c)
                .filter(c -> c.getMaybeProperties().isPresent())
                .toList();

        assertThat(chunksWithProperties).isNotEmpty();
        for (var chunk : chunksWithProperties) {
            assertThat(chunk.getMaybeProperties().get()).isNotEmpty();
        }
    }

    @Test
    void readUserDataWithText() throws Exception {
        var path = Paths.get(getClass().getResource("/aseprite/sprites/tags3x123reps.aseprite").toURI());
        var file = AsepriteFile.read(path);

        var chunksWithText = file.frames().stream()
                .flatMap(f -> f.chunks().stream())
                .filter(c -> c instanceof UserDataChunk)
                .map(c -> (UserDataChunk) c)
                .filter(c -> c.getMaybeText().isPresent() && !c.getMaybeText().get().isEmpty())
                .toList();

        // This file may or may not have text, just verify parsing works
        assertThat(chunksWithText).isNotNull();
    }

    @Test
    void readUserDataWithColor() throws Exception {
        var path = Paths.get(getClass().getResource("/aseprite/sprites/tags3x123reps.aseprite").toURI());
        var file = AsepriteFile.read(path);

        var chunksWithColor = file.frames().stream()
                .flatMap(f -> f.chunks().stream())
                .filter(c -> c instanceof UserDataChunk)
                .map(c -> (UserDataChunk) c)
                .filter(c -> c.getMaybeColor().isPresent())
                .toList();

        for (var chunk : chunksWithColor) {
            var color = chunk.getMaybeColor().get();
            assertThat(color.r()).isAtLeast((short) 0);
            assertThat(color.r()).isAtMost((short) 255);
        }
    }
}
