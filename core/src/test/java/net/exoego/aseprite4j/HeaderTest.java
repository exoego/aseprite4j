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
    void addition(String filename, int expectedSize) throws URISyntaxException {
        var path = Paths.get(HeaderTest.class.getResource("/aseprite/sprites/" + filename + ".aseprite").toURI());
        var header = Header.read(path);
        assertThat(header.getFileSize()).isEqualTo(expectedSize);
    }
}
