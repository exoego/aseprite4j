package net.exoego.aseprite4j;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.net.URISyntaxException;
import java.nio.file.Paths;

public class HeaderTest {
    @ParameterizedTest
    @CsvSource({
            "cut_paste, 348",
            "1empty3, 1048",
            "tags3x123reps, 1282",
            "file-tests-props, 2300",
    })
    void getFileSize(String filename, int expectedSize) throws URISyntaxException {
        var path = Paths.get(HeaderTest.class.getResource("/aseprite/sprites/" + filename + ".aseprite").toURI());
        var header = Header.read(path);
        assertThat(header.getFileSize()).isEqualTo(expectedSize);
    }

    @ParameterizedTest
    @CsvSource({
            "cut_paste, 1",
            "1empty3, 3",
            "tags3x123reps, 9",
            "file-tests-props, 6",
            "point4frames, 4",
            "2f-index-3x3, 2",
            "4f-index-4x4, 4",
            "slices-moving, 4",
            "2x2tilemap2x2tile, 1"
    })
    void getFrames(String filename, int expectedSize) throws URISyntaxException {
        var path = Paths.get(HeaderTest.class.getResource("/aseprite/sprites/" + filename + ".aseprite").toURI());
        var header = Header.read(path);
        assertThat(header.getFrames()).isEqualTo(expectedSize);
    }
}
