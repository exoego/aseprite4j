package net.exoego.aseprite4j;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;

public class MaskChunkTest {

    @Test
    void readSimpleMask() throws IOException {
        var baos = new ByteArrayOutputStream();
        // SHORT x = 10
        baos.write(new byte[]{0x0A, 0x00});
        // SHORT y = 20
        baos.write(new byte[]{0x14, 0x00});
        // WORD width = 8
        baos.write(new byte[]{0x08, 0x00});
        // WORD height = 2
        baos.write(new byte[]{0x02, 0x00});
        // 8 bytes reserved
        baos.write(new byte[8]);
        // STRING name = "mask1" (WORD length + bytes)
        baos.write(new byte[]{0x05, 0x00}); // length = 5
        baos.write("mask1".getBytes());
        // Bitmap data: width=8, so rowBytes = (8+7)/8 = 1, height=2, so 2 bytes total
        baos.write(new byte[]{(byte) 0xFF, (byte) 0xAA}); // all bits set in row 0, alternating in row 1

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        var chunk = MaskChunk.build(reader);

        assertThat(chunk.x()).isEqualTo(10);
        assertThat(chunk.y()).isEqualTo(20);
        assertThat(chunk.width()).isEqualTo(8);
        assertThat(chunk.height()).isEqualTo(2);
        assertThat(chunk.name()).isEqualTo("mask1");
        assertThat(chunk.bitmapData()).hasLength(2);
        assertThat(chunk.bitmapData()[0]).isEqualTo((byte) 0xFF);
        assertThat(chunk.bitmapData()[1]).isEqualTo((byte) 0xAA);
    }

    @Test
    void readMaskWithNegativeCoordinates() throws IOException {
        var baos = new ByteArrayOutputStream();
        // SHORT x = -5 (0xFFFB in two's complement)
        baos.write(new byte[]{(byte) 0xFB, (byte) 0xFF});
        // SHORT y = -10 (0xFFF6)
        baos.write(new byte[]{(byte) 0xF6, (byte) 0xFF});
        // WORD width = 16
        baos.write(new byte[]{0x10, 0x00});
        // WORD height = 1
        baos.write(new byte[]{0x01, 0x00});
        // 8 bytes reserved
        baos.write(new byte[8]);
        // STRING name = "" (empty)
        baos.write(new byte[]{0x00, 0x00}); // length = 0
        // Bitmap data: width=16, rowBytes = (16+7)/8 = 2, height=1, so 2 bytes
        baos.write(new byte[]{0x55, 0x55});

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        var chunk = MaskChunk.build(reader);

        assertThat(chunk.x()).isEqualTo(-5);
        assertThat(chunk.y()).isEqualTo(-10);
        assertThat(chunk.width()).isEqualTo(16);
        assertThat(chunk.height()).isEqualTo(1);
        assertThat(chunk.name()).isEmpty();
        assertThat(chunk.bitmapData()).hasLength(2);
    }

    @Test
    void readMaskWithNonByteAlignedWidth() throws IOException {
        var baos = new ByteArrayOutputStream();
        // SHORT x = 0
        baos.write(new byte[]{0x00, 0x00});
        // SHORT y = 0
        baos.write(new byte[]{0x00, 0x00});
        // WORD width = 12 (not byte-aligned, rowBytes = (12+7)/8 = 2)
        baos.write(new byte[]{0x0C, 0x00});
        // WORD height = 3
        baos.write(new byte[]{0x03, 0x00});
        // 8 bytes reserved
        baos.write(new byte[8]);
        // STRING name = "test"
        baos.write(new byte[]{0x04, 0x00}); // length = 4
        baos.write("test".getBytes());
        // Bitmap data: rowBytes=2, height=3, so 6 bytes
        baos.write(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06});

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        var chunk = MaskChunk.build(reader);

        assertThat(chunk.width()).isEqualTo(12);
        assertThat(chunk.height()).isEqualTo(3);
        assertThat(chunk.bitmapData()).hasLength(6);
    }
}
