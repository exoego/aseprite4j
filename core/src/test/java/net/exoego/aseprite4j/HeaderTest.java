package net.exoego.aseprite4j;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayInputStream;
import java.nio.file.Paths;

public class HeaderTest {
    @Test
    public void throwsInvalidMagicNumber() {
        var bios = new ByteArrayInputStream(new byte[]{
                0x12, 0x00, 0x00, 0x00, // bytes in this frame
                (byte) 0, (byte) 0, // magic number
        });
        assertThrows(IllegalArgumentException.class, () -> Header.read(new InputStreamReader(bios)));
    }

    @ParameterizedTest
    @CsvSource({
            "cut_paste, 348",
            "1empty3, 1048",
            "tags3x123reps, 1282",
            "file-tests-props, 2300",
    })
    void getFileSizeInBytes(String filename, int expected) throws Exception {
        var path = Paths.get(HeaderTest.class.getResource("/aseprite/sprites/" + filename + ".aseprite").toURI());
        var header = Header.read(path);
        assertThat(header.getFileSizeInBytes()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "1empty3, 3",
            "2f-index-3x3, 2",
            "2x2tilemap2x2tile, 1",
            "4f-index-4x4, 4",
            "cut_paste, 1",
            "file-tests-props, 6",
            "point4frames, 4",
            "slices-moving, 4",
            "tags3x123reps, 9",
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
            "my-w128-h64-grayscale, 128",
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
            "my-w128-h64-grayscale, 64",
    })
    void getImageHeight(String filename, int expected) throws Exception {
        var path = Paths.get(HeaderTest.class.getResource("/aseprite/sprites/" + filename + ".aseprite").toURI());
        var header = Header.read(path);
        assertThat(header.getImageHeight()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "1empty3, RGBA, 32",
            "2f-index-3x3, Indexed, 8",
            "my-w128-h64-grayscale, Grayscale, 16",
    })
    void getColorDepth(String filename, ColorDepth expected, int expectedRawValue) throws Exception {
        var path = Paths.get(HeaderTest.class.getResource("/aseprite/sprites/" + filename + ".aseprite").toURI());
        var header = Header.read(path);
        assertThat(header.getColorDepth()).isEqualTo(expected);
        assertThat(header.getColorDepth().getBitsPerPixel()).isEqualTo(expectedRawValue);
    }

    @ParameterizedTest
    @CsvSource({
            "1empty3, 1",
            "2f-index-3x3, 1",
    })
    void getRawFlags(String filename, int expected) throws Exception {
        var path = Paths.get(HeaderTest.class.getResource("/aseprite/sprites/" + filename + ".aseprite").toURI());
        var header = Header.read(path);
        assertThat(header.getRawFlags()).isEqualTo(expected);

        if ((int) header.getRawFlags() == 1) {
            assertThat(header.getFlagSet()).containsExactly(HeaderFlag.LayerOpacityValid);
        } else {
            // TODO: Add more tests for other flags
            fail("Please update this test to assert other flags");
        }
    }

    @ParameterizedTest
    @CsvSource({
            "1empty3, 0",
            "2f-index-3x3, 21",
            "my-w128-h64-grayscale, 0",
    })
    void getTransparentColorIndex(String filename, int expected) throws Exception {
        var path = Paths.get(HeaderTest.class.getResource("/aseprite/sprites/" + filename + ".aseprite").toURI());
        var header = Header.read(path);
        assertThat(header.getTransparentColorIndex()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "1empty3, 32",
            "2f-index-3x3, 32",
            "bg-index-3, 4",
            "my-w128-h64-grayscale, 256",
    })
    void getNumberOfColors(String filename, int expected) throws Exception {
        var path = Paths.get(HeaderTest.class.getResource("/aseprite/sprites/" + filename + ".aseprite").toURI());
        var header = Header.read(path);
        assertThat(header.getNumberOfColors()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "1empty3, 1, 1, 1",
            "my-w128-h64-grayscale, 1, 1, 1",
            "my-w100-h200, 1, 1, 1",
            "my-double-height, 1, 2, 0.5",
            "my-double-wide, 2, 1, 2.0",
    })
    void getPixelWidthAndHeight(String filename, int expectedWidth, int expectedHeight, double expectedAspectRatio) throws Exception {
        var path = Paths.get(HeaderTest.class.getResource("/aseprite/sprites/" + filename + ".aseprite").toURI());
        var header = Header.read(path);
        assertThat(header.getPixelWidth()).isEqualTo(expectedWidth);
        assertThat(header.getPixelHeight()).isEqualTo(expectedHeight);
        assertThat(header.getPixelHeight()).isEqualTo(expectedHeight);
        assertThat(header.getPixelAspectRatio()).isEqualTo(expectedAspectRatio);
    }

    @ParameterizedTest
    @CsvSource({
            "my-w100-h200,          0,  0, 16, 16",
            "my-w128-h64-grayscale, 5, 10, 24, 12",
    })
    void getGridXandY(String filename, int expectedX, int expectedY, int expectedWidth, int expectedHeight) throws Exception {
        var path = Paths.get(HeaderTest.class.getResource("/aseprite/sprites/" + filename + ".aseprite").toURI());
        var header = Header.read(path);
        assertThat(header.getGridX()).isEqualTo(expectedX);
        assertThat(header.getGridY()).isEqualTo(expectedY);
        assertThat(header.getGridWidth()).isEqualTo(expectedWidth);
        assertThat(header.getGridHeight()).isEqualTo(expectedHeight);
    }
}
