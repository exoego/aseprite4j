package net.exoego.aseprite4j;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Paths;

import static com.google.common.truth.Truth.assertThat;

public class FrameTest {
//    @Test
//    public void basicRead() throws IOException {
//        var bios = new ByteArrayInputStream(new byte[]{
//                0x12, 0x00, 0x00, 0x00, // bytes in this frame
//                (byte) 0xFA, (byte) 0xF1, // magic number
//                0x01, 0x00, // old num of chunks
//                0x01, 0x00, // frame duration
//                0x00, 0x00, // future
//                0x00, 0x00, 0x00, 0x00, // new num of chunks
//                // chunks
//                0x0a, 0x00, 0x00, 0x00, // chunk size
//                0x00, 0x00, // chunk type
//                0x00, 0x00, 0x00, 0x00, // chunk data
//        });
//        var frameHeader = Frame.read(new InputStreamReader(bios), ColorDepth.Indexed).header();
//        assertThat(frameHeader.bytesInThisFrame()).isEqualTo(18);
//        assertThat(frameHeader.frameDuration()).isEqualTo(1);
//        assertThat(frameHeader.numberOfChunks()).isEqualTo(1);
//    }
//
//    @ParameterizedTest
//    @CsvSource({
////            "2x2tilemap2x2tile, 1",
//            "2f-index-3x3, 2",
////            "1empty3, 3",
////            "4f-index-4x4, 4",
////            "file-tests-props, 6",
////            "tags3x123reps, 9",
//    })
//    void getFrames(String filename, int expectedNumFrames) throws Exception {
//        var path = Paths.get(FrameTest.class.getResource("/aseprite/sprites/" + filename + ".aseprite").toURI());
//        var file = AsepriteFile.read(path);
//        assertThat(file.header().numberOfFrames()).isEqualTo(expectedNumFrames);
//        assertThat(file.frames()).hasSize(expectedNumFrames);
//    }
//
//    @Test
//    void basicFrameChunk_cut_paste() throws Exception {
//        var path = Paths.get(FrameTest.class.getResource("/aseprite/sprites/cut_paste.aseprite").toURI());
//        var file = AsepriteFile.read(path);
//        assertThat(file.frames()).hasSize(1);
//        var frame = file.frames().get(0);
//        var frameHeader = frame.header();
//        assertThat(frameHeader.bytesInThisFrame()).isEqualTo(220);
//        assertThat(frameHeader.numberOfChunks()).isEqualTo(6);
//        assertThat(frameHeader.frameDuration()).isEqualTo(100);
//
//        var frameChunks = frame.chunks();
//        assertThat(frameChunks).hasSize(6);
//        assertThat(frameChunks.get(0)).isInstanceOf(ColorProfileChunk.class);
//        assertThat(frameChunks.get(1)).isInstanceOf(PaletteChunk.class);
//        assertThat(frameChunks.get(2)).isInstanceOf(LayerChunk.class);
//        assertThat(frameChunks.get(3)).isInstanceOf(LayerChunk.class);
//        assertThat(frameChunks.get(4)).isInstanceOf(CelChunk.class);
//        assertThat(frameChunks.get(5)).isInstanceOf(CelChunk.class);
//    }

    @Test
    void basicFrameChunk() throws Exception {
        var path = Paths.get(FrameTest.class.getResource("/aseprite/sprites/2x2tilemap2x2tile.aseprite").toURI());
        var file = AsepriteFile.read(path);
        assertThat(file.frames()).hasSize(1);
        var frame = file.frames().get(0);
        var frameHeader = frame.header();
        assertThat(frameHeader.bytesInThisFrame()).isEqualTo(616);
        assertThat(frameHeader.numberOfChunks()).isEqualTo(12);
        assertThat(frameHeader.frameDuration()).isEqualTo(100);

        var frameChunks = frame.chunks();
        assertThat(frameChunks).hasSize(12);
        assertThat(frameChunks.get(0)).isInstanceOf(ColorProfileChunk.class);
        assertThat(frameChunks.get(1)).isInstanceOf(PaletteChunk.class);
        assertThat(frameChunks.get(2)).isInstanceOf(OldPaletteChunk4.class);
        assertThat(frameChunks.get(3)).isInstanceOf(TilesetChunk.class);
        assertThat(frameChunks.get(4)).isInstanceOf(UserDataChunk.class);
        assertThat(frameChunks.get(5)).isInstanceOf(UserDataChunk.class);
        assertThat(frameChunks.get(6)).isInstanceOf(UserDataChunk.class);
        assertThat(frameChunks.get(7)).isInstanceOf(LayerChunk.class);
        assertThat(frameChunks.get(8)).isInstanceOf(CelChunk.class);
        assertThat(frameChunks.get(9)).isInstanceOf(UnknownChunk.class);
        assertThat(frameChunks.get(10)).isInstanceOf(UnknownChunk.class);
        assertThat(frameChunks.get(11)).isInstanceOf(UnknownChunk.class);
    }
}
