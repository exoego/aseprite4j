package net.exoego.aseprite4j;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Paths;

import static com.google.common.truth.Truth.assertThat;

public class SliceChunkTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "file-tests-props",
            "slices",
            "slices-moving"
    })
    void readSliceChunk(String filename) throws Exception {
        var path = Paths.get(getClass().getResource("/aseprite/sprites/" + filename + ".aseprite").toURI());
        var file = AsepriteFile.read(path);

        var chunks = file.frames().stream()
                .flatMap(f -> f.chunks().stream())
                .filter(c -> c instanceof SliceChunk)
                .map(c -> (SliceChunk) c)
                .toList();

        assertThat(chunks).isNotEmpty();
        for (var chunk : chunks) {
            assertThat(chunk.name()).isNotNull();
            assertThat(chunk.keys()).isNotEmpty();
        }
    }

    @Test
    void readSliceWithKeys() throws Exception {
        var path = Paths.get(getClass().getResource("/aseprite/sprites/slices.aseprite").toURI());
        var file = AsepriteFile.read(path);

        var chunks = file.frames().stream()
                .flatMap(f -> f.chunks().stream())
                .filter(c -> c instanceof SliceChunk)
                .map(c -> (SliceChunk) c)
                .toList();

        assertThat(chunks).isNotEmpty();
        for (var chunk : chunks) {
            for (var key : chunk.keys()) {
                assertThat(key.frameNumber()).isAtLeast(0);
                assertThat(key.sliceWidth()).isAtLeast(0);
                assertThat(key.sliceHeight()).isAtLeast(0);
            }
        }
    }

    @Test
    void readSliceWith9Patches() throws Exception {
        var path = Paths.get(getClass().getResource("/aseprite/sprites/slices.aseprite").toURI());
        var file = AsepriteFile.read(path);

        var chunksWithNinePatch = file.frames().stream()
                .flatMap(f -> f.chunks().stream())
                .filter(c -> c instanceof SliceChunk)
                .map(c -> (SliceChunk) c)
                .filter(SliceChunk::has9Patches)
                .toList();

        for (var chunk : chunksWithNinePatch) {
            for (var key : chunk.keys()) {
                assertThat(key.ninePatch()).isPresent();
            }
        }
    }

    @Test
    void readSliceWithPivot() throws Exception {
        var path = Paths.get(getClass().getResource("/aseprite/sprites/slices.aseprite").toURI());
        var file = AsepriteFile.read(path);

        var chunksWithPivot = file.frames().stream()
                .flatMap(f -> f.chunks().stream())
                .filter(c -> c instanceof SliceChunk)
                .map(c -> (SliceChunk) c)
                .filter(SliceChunk::hasPivot)
                .toList();

        for (var chunk : chunksWithPivot) {
            for (var key : chunk.keys()) {
                assertThat(key.pivot()).isPresent();
            }
        }
    }
}
