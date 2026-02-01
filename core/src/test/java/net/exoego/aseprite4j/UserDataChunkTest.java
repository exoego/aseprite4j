package net.exoego.aseprite4j;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static com.google.common.truth.Truth.assertThat;

public class UserDataChunkTest {

    @Test
    void readUserDataWithProperties() throws Exception {
        var path = Paths.get(UserDataChunkTest.class.getResource("/aseprite/sprites/file-tests-props.aseprite").toURI());
        var file = AsepriteFile.read(path);

        var userDataChunks = file.frames().stream()
                .flatMap(f -> f.chunks().stream())
                .filter(c -> c instanceof UserDataChunk)
                .map(c -> (UserDataChunk) c)
                .toList();

        assertThat(userDataChunks).isNotEmpty();
    }

    @Test
    void readUserDataFromTilemapFile() throws Exception {
        var path = Paths.get(UserDataChunkTest.class.getResource("/aseprite/sprites/2x2tilemap2x2tile.aseprite").toURI());
        var file = AsepriteFile.read(path);

        var userDataChunks = file.frames().get(0).chunks().stream()
                .filter(c -> c instanceof UserDataChunk)
                .map(c -> (UserDataChunk) c)
                .toList();

        // This file should have multiple UserDataChunks (after tileset)
        assertThat(userDataChunks).isNotEmpty();
    }
}
