package net.exoego.aseprite4j;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Paths;

import static com.google.common.truth.Truth.assertThat;

public class TagsChunkTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "1empty3",
            "file-tests-props",
            "link",
            "tags3",
            "tags3x123reps"
    })
    void readTagsChunk(String filename) throws Exception {
        var path = Paths.get(getClass().getResource("/aseprite/sprites/" + filename + ".aseprite").toURI());
        var file = AsepriteFile.read(path);

        var chunks = file.frames().get(0).chunks().stream()
                .filter(c -> c instanceof TagsChunk)
                .map(c -> (TagsChunk) c)
                .toList();

        assertThat(chunks).isNotEmpty();
        for (var chunk : chunks) {
            assertThat(chunk.tags()).isNotNull();
        }
    }
}
