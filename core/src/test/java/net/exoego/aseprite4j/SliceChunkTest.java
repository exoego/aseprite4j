package net.exoego.aseprite4j;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static com.google.common.truth.Truth.assertThat;

public class SliceChunkTest {

    @Test
    void readSlicesFile() throws Exception {
        var path = Paths.get(SliceChunkTest.class.getResource("/aseprite/sprites/slices.aseprite").toURI());
        var file = AsepriteFile.read(path);

        var sliceChunks = file.frames().get(0).chunks().stream()
                .filter(c -> c instanceof SliceChunk)
                .map(c -> (SliceChunk) c)
                .toList();

        assertThat(sliceChunks).isNotEmpty();
    }

    @Test
    void readSlicesMovingFile() throws Exception {
        var path = Paths.get(SliceChunkTest.class.getResource("/aseprite/sprites/slices-moving.aseprite").toURI());
        var file = AsepriteFile.read(path);

        var sliceChunks = file.frames().stream()
                .flatMap(f -> f.chunks().stream())
                .filter(c -> c instanceof SliceChunk)
                .map(c -> (SliceChunk) c)
                .toList();

        assertThat(sliceChunks).isNotEmpty();
    }
}
