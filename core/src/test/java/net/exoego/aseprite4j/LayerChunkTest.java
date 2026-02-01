package net.exoego.aseprite4j;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Paths;

import static com.google.common.truth.Truth.assertThat;

public class LayerChunkTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "1empty3",
            "2f-index-3x3",
            "2x2-index-2frame",
            "2x2tilemap2x2tile",
            "2x3tilemap-indexed",
            "3x2tilemap-grayscale",
            "4f-index-4x4",
            "abcd",
            "bg-index-3",
            "cut_paste",
            "file-tests-props",
            "groups2",
            "groups3abc",
            "link",
            "my-double-height",
            "my-double-wide",
            "my-w100-h200",
            "my-w128-h64-grayscale",
            "point2frames",
            "point4frames",
            "slices",
            "slices-moving",
            "tags3",
            "tags3x123reps"
    })
    void readLayerChunk(String filename) throws Exception {
        var path = Paths.get(getClass().getResource("/aseprite/sprites/" + filename + ".aseprite").toURI());
        var file = AsepriteFile.read(path);

        var chunks = file.frames().get(0).chunks().stream()
                .filter(c -> c instanceof LayerChunk)
                .map(c -> (LayerChunk) c)
                .toList();

        assertThat(chunks).isNotEmpty();
        for (var chunk : chunks) {
            assertThat(chunk.name()).isNotNull();
            assertThat(chunk.type()).isNotNull();
            assertThat(chunk.blendMode()).isNotNull();
            assertThat(chunk.opacity()).isAtLeast((short) 0);
            assertThat(chunk.opacity()).isAtMost((short) 255);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "groups2",
            "groups3abc"
    })
    void readLayerWithGroups(String filename) throws Exception {
        var path = Paths.get(getClass().getResource("/aseprite/sprites/" + filename + ".aseprite").toURI());
        var file = AsepriteFile.read(path);

        var chunks = file.frames().get(0).chunks().stream()
                .filter(c -> c instanceof LayerChunk)
                .map(c -> (LayerChunk) c)
                .toList();

        var groupLayers = chunks.stream()
                .filter(c -> c.type() == LayerType.GROUP)
                .toList();

        assertThat(groupLayers).isNotEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2x2tilemap2x2tile",
            "2x3tilemap-indexed",
            "3x2tilemap-grayscale"
    })
    void readTilemapLayer(String filename) throws Exception {
        var path = Paths.get(getClass().getResource("/aseprite/sprites/" + filename + ".aseprite").toURI());
        var file = AsepriteFile.read(path);

        var tilemapLayers = file.frames().get(0).chunks().stream()
                .filter(c -> c instanceof LayerChunk)
                .map(c -> (LayerChunk) c)
                .filter(c -> c.type() == LayerType.TILEMAP)
                .toList();

        assertThat(tilemapLayers).isNotEmpty();
    }
}
