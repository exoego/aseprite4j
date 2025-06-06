package net.exoego.aseprite4j;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Paths;

import static com.google.common.truth.Truth.assertThat;

public class FrameTest {
    @Test
    public void basicRead() throws IOException {
        var bios = new ByteArrayInputStream(new byte[]{
                0x12, 0x00, 0x00, 0x00, // bytes in this frame
                (byte) 0xFA, (byte) 0xF1, // magic number
                0x01, 0x00, // old num of chunks
                0x01, 0x00, // frame duration
                0x00, 0x00, // future
                0x00, 0x00, 0x00, 0x00, // new num of chunks
                // chunks
                0x0a, 0x00, 0x00, 0x00, // chunk size
                0x00, 0x00, // chunk type
                0x00, 0x00, 0x00, 0x00, // chunk data
        });
        var frame = FrameImpl.read(new InputStreamReader(bios), ColorDepth.Indexed);
        assertThat(frame.header().bytesInThisFrame()).isEqualTo(18);
        assertThat(frame.header().frameDuration()).isEqualTo(1);
        assertThat(frame.header().getNumberOfChunks()).isEqualTo(1);
    }

    @ParameterizedTest
    @CsvSource({
            "2x2tilemap2x2tile, 1",
//            "2f-index-3x3, 2",
//            "1empty3, 3",
//            "4f-index-4x4, 4",
//            "file-tests-props, 6",
//            "tags3x123reps, 9",
    })
    void getFrames(String filename, int expected) throws Exception {
        var path = Paths.get(FrameTest.class.getResource("/aseprite/sprites/" + filename + ".aseprite").toURI());
        var file = AsepriteFile.read(path);
        assertThat(file.frames()).hasSize(file.header().numberOfFrames());
        assertThat(file.frames()).hasSize(expected);
    }
}
