package net.exoego.aseprite4j;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Paths;

import static com.google.common.truth.Truth.assertThat;

public class CelChunkTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "1empty3",
            "2f-index-3x3",
            "2x2-index-2frame",
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
    void readCompressedImageCelChunk(String filename) throws Exception {
        var path = Paths.get(getClass().getResource("/aseprite/sprites/" + filename + ".aseprite").toURI());
        var file = AsepriteFile.read(path);

        var chunks = file.frames().stream()
                .flatMap(f -> f.chunks().stream())
                .filter(c -> c instanceof CelChunk.CompressedImageCelChunk)
                .map(c -> (CelChunk.CompressedImageCelChunk) c)
                .toList();

        assertThat(chunks).isNotEmpty();
        for (var chunk : chunks) {
            assertThat(chunk.widthInPixels()).isGreaterThan(0);
            assertThat(chunk.heightInPixels()).isGreaterThan(0);
            assertThat(chunk.pixelData()).isNotNull();
            assertThat(chunk.pixelData().length).isEqualTo(chunk.widthInPixels() * chunk.heightInPixels());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2x2tilemap2x2tile",
            "2x3tilemap-indexed",
            "file-tests-props"
    })
    void readCompressedTilemapCelChunk(String filename) throws Exception {
        var path = Paths.get(getClass().getResource("/aseprite/sprites/" + filename + ".aseprite").toURI());
        var file = AsepriteFile.read(path);

        var chunks = file.frames().stream()
                .flatMap(f -> f.chunks().stream())
                .filter(c -> c instanceof CelChunk.CompressedTilemapCelChunk)
                .map(c -> (CelChunk.CompressedTilemapCelChunk) c)
                .toList();

        assertThat(chunks).isNotEmpty();
        for (var chunk : chunks) {
            assertThat(chunk.widthInNumberOfTiles()).isGreaterThan(0);
            assertThat(chunk.heightInNumberOfTiles()).isGreaterThan(0);
            assertThat(chunk.tileData()).isNotNull();
            assertThat(chunk.tileData().length).isEqualTo(chunk.widthInNumberOfTiles() * chunk.heightInNumberOfTiles());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "link",
            "slices-moving",
            "tags3",
            "tags3x123reps"
    })
    void readLinkedCelChunk(String filename) throws Exception {
        var path = Paths.get(getClass().getResource("/aseprite/sprites/" + filename + ".aseprite").toURI());
        var file = AsepriteFile.read(path);

        var chunks = file.frames().stream()
                .flatMap(f -> f.chunks().stream())
                .filter(c -> c instanceof CelChunk.LinkedCelCelChunk)
                .map(c -> (CelChunk.LinkedCelCelChunk) c)
                .toList();

        assertThat(chunks).isNotEmpty();
        for (var chunk : chunks) {
            assertThat(chunk.framePositionToLinkWith()).isAtLeast(0);
        }
    }
}
