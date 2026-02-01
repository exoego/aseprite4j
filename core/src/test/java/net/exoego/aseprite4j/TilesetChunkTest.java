package net.exoego.aseprite4j;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static com.google.common.truth.Truth.assertThat;

public class TilesetChunkTest {

    @Test
    void readTilesetChunk() throws Exception {
        var path = Paths.get(TilesetChunkTest.class.getResource("/aseprite/sprites/2x2tilemap2x2tile.aseprite").toURI());
        var file = AsepriteFile.read(path);

        var tilesetChunks = file.frames().get(0).chunks().stream()
                .filter(c -> c instanceof TilesetChunk)
                .map(c -> (TilesetChunk) c)
                .toList();

        assertThat(tilesetChunks).hasSize(1);

        var tileset = tilesetChunks.get(0);
        assertThat(tileset.tileWidth()).isEqualTo(2);
        assertThat(tileset.tileHeight()).isEqualTo(2);
        assertThat(tileset.numberOfTiles()).isGreaterThan(0);
        assertThat(tileset.name()).isNotNull();

        // Should have tileset image if flag is set
        if (tileset.tilesetFlagSet().contains(TilesetFlag.INCLUDE_TILES_INSIDE_THIS_FILE)) {
            assertThat(tileset.tilesetImage()).isPresent();
            assertThat(tileset.tilesetImage().get().length).isGreaterThan(0);
        }
    }
}
