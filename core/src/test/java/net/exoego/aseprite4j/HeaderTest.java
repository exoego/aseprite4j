package net.exoego.aseprite4j;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.nio.file.Paths;

public class HeaderTest {
    @ParameterizedTest
    @CsvSource({
            "cut_paste, 348",
            "1empty3, 1048",
            "tags3x123reps, 1282",
            "file-tests-props, 2300",
    })
    void getFileSize(String filename, int expected) throws Exception {
        var path = Paths.get(HeaderTest.class.getResource("/aseprite/sprites/" + filename + ".aseprite").toURI());
        var header = Header.read(path);
        assertThat(header.getFileSize()).isEqualTo(expected);
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
    void getNumberOfFrames(String filename, int expected) throws Exception {
        var path = Paths.get(HeaderTest.class.getResource("/aseprite/sprites/" + filename + ".aseprite").toURI());
        var header = Header.read(path);
        assertThat(header.getNumberOfFrames()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "cut_paste, 5",
            "my-w100-h200, 100",
    })
    void getImageWidth(String filename, int expected) throws Exception {
        var path = Paths.get(HeaderTest.class.getResource("/aseprite/sprites/" + filename + ".aseprite").toURI());
        var header = Header.read(path);
        assertThat(header.getImageWidth()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "cut_paste, 5",
            "my-w100-h200, 200",
    })
    void getImageHeight(String filename, int expected) throws Exception {
        var path = Paths.get(HeaderTest.class.getResource("/aseprite/sprites/" + filename + ".aseprite").toURI());
        var header = Header.read(path);
        assertThat(header.getImageHeight()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "1empty3, RGBA",
            "2f-index-3x3, Indexed",
            "my-w128-h64-grayscale, Grayscale",
    })
    void getColorDepth(String filename, ColorDepth expected) throws Exception {
        var path = Paths.get(HeaderTest.class.getResource("/aseprite/sprites/" + filename + ".aseprite").toURI());
        var header = Header.read(path);
        assertThat(header.getColorDepth()).isEqualTo(expected);
    }

}
