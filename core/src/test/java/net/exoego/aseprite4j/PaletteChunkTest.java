package net.exoego.aseprite4j;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static com.google.common.truth.Truth.assertThat;

public class PaletteChunkTest {

    @Test
    void readPaletteChunk() throws Exception {
        var path = Paths.get(PaletteChunkTest.class.getResource("/aseprite/sprites/cut_paste.aseprite").toURI());
        var file = AsepriteFile.read(path);

        var paletteChunks = file.frames().get(0).chunks().stream()
                .filter(c -> c instanceof PaletteChunk)
                .map(c -> (PaletteChunk) c)
                .toList();

        assertThat(paletteChunks).hasSize(1);

        var palette = paletteChunks.get(0);
        assertThat(palette.paletteSize()).isGreaterThan(0);
        assertThat(palette.entries()).isNotEmpty();

        // Check that entries have valid colors
        for (var entry : palette.entries()) {
            var color = entry.color();
            assertThat(color.r()).isAtLeast((short) 0);
            assertThat(color.r()).isAtMost((short) 255);
            assertThat(color.g()).isAtLeast((short) 0);
            assertThat(color.g()).isAtMost((short) 255);
            assertThat(color.b()).isAtLeast((short) 0);
            assertThat(color.b()).isAtMost((short) 255);
            assertThat(color.a()).isAtLeast((short) 0);
            assertThat(color.a()).isAtMost((short) 255);
        }
    }

    @Test
    void readPaletteWithMultipleEntries() throws Exception {
        var path = Paths.get(PaletteChunkTest.class.getResource("/aseprite/sprites/2x2tilemap2x2tile.aseprite").toURI());
        var file = AsepriteFile.read(path);

        var paletteChunks = file.frames().get(0).chunks().stream()
                .filter(c -> c instanceof PaletteChunk)
                .map(c -> (PaletteChunk) c)
                .toList();

        assertThat(paletteChunks).hasSize(1);
        assertThat(paletteChunks.get(0).entries()).isNotEmpty();
    }
}
