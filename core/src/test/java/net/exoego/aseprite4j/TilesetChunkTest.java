package net.exoego.aseprite4j;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Paths;

import static com.google.common.truth.Truth.assertThat;

public class TilesetChunkTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "2x2tilemap2x2tile",
            "2x3tilemap-indexed",
            "3x2tilemap-grayscale",
            "file-tests-props"
    })
    void readTilesetChunk(String filename) throws Exception {
        var path = Paths.get(getClass().getResource("/aseprite/sprites/" + filename + ".aseprite").toURI());
        var file = AsepriteFile.read(path);

        var chunks = file.frames().get(0).chunks().stream()
                .filter(c -> c instanceof TilesetChunk)
                .map(c -> (TilesetChunk) c)
                .toList();

        assertThat(chunks).isNotEmpty();
        for (var chunk : chunks) {
            assertThat(chunk.tileWidth()).isGreaterThan(0);
            assertThat(chunk.tileHeight()).isGreaterThan(0);
            assertThat(chunk.numberOfTiles()).isGreaterThan(0);
        }
    }

    @Test
    void readTilesetWithImage() throws Exception {
        var path = Paths.get(getClass().getResource("/aseprite/sprites/2x2tilemap2x2tile.aseprite").toURI());
        var file = AsepriteFile.read(path);

        var chunks = file.frames().get(0).chunks().stream()
                .filter(c -> c instanceof TilesetChunk)
                .map(c -> (TilesetChunk) c)
                .filter(c -> c.tilesetFlagSet().contains(TilesetFlag.INCLUDE_TILES_INSIDE_THIS_FILE))
                .toList();

        for (var chunk : chunks) {
            assertThat(chunk.tilesetImage()).isPresent();
            assertThat(chunk.tilesetImage().get().length).isGreaterThan(0);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2x3tilemap-indexed",
            "3x2tilemap-grayscale"
    })
    void readTilesetFromDifferentColorDepths(String filename) throws Exception {
        var path = Paths.get(getClass().getResource("/aseprite/sprites/" + filename + ".aseprite").toURI());
        var file = AsepriteFile.read(path);

        var chunks = file.frames().get(0).chunks().stream()
                .filter(c -> c instanceof TilesetChunk)
                .map(c -> (TilesetChunk) c)
                .toList();

        assertThat(chunks).isNotEmpty();
        for (var chunk : chunks) {
            if (chunk.tilesetFlagSet().contains(TilesetFlag.INCLUDE_TILES_INSIDE_THIS_FILE)) {
                assertThat(chunk.tilesetImage()).isPresent();
                int expectedPixels = chunk.tileWidth() * chunk.tileHeight() * (int) chunk.numberOfTiles();
                assertThat(chunk.tilesetImage().get().length).isEqualTo(expectedPixels);
            }
        }
    }
}
