package net.exoego.aseprite4j;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static com.google.common.truth.Truth.assertThat;

public class FrameHeaderTest {
    @Test
    public void basicRead() {
        var bios = new ByteArrayInputStream(new byte[]{
                0x12, 0x00, 0x00, 0x00, // bytes in this frame
                (byte) 0xFA, (byte) 0xF1, // magic number
                0x11, 0x00, // old num of chunks
                0x01, 0x00, // frame duration
                0x00, 0x00, // future
                0x00, 0x00, 0x00, 0x00, // new num of chunks
        });
        var header = FrameHeaderImpl.read(new InputStreamReader(bios));
        assertThat(header.bytesInThisFrame()).isEqualTo(18);
        assertThat(header.frameDuration()).isEqualTo(1);
        assertThat(header.getNumberOfChunks()).isEqualTo(17);
    }

    @Test
    public void newGetNumberOfChunks() {
        var bios = new ByteArrayInputStream(new byte[]{
                0x41, 0x00, 0x00, 0x00, // bytes in this frame
                (byte) 0xFA, (byte) 0xF1, // magic number
                (byte) 0xFF, (byte) 0xFF, // old num of chunks
                0x00, 0x10, // frame duration
                0x00, 0x00, // future
                (byte) 0xFF, (byte) 0xFF, 0x01, 0x00, // new num of chunks
        });
        var header = FrameHeaderImpl.read(new InputStreamReader(bios));
        assertThat(header.bytesInThisFrame()).isEqualTo(65);
        assertThat(header.frameDuration()).isEqualTo(4096);
        assertThat(header.getNumberOfChunks()).isEqualTo(131071);
    }
}
