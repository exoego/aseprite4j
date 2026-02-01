package net.exoego.aseprite4j;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

public class CelExtraChunkTest {

    @Test
    void readCelExtraWithPreciseBounds() throws IOException {
        var baos = new ByteArrayOutputStream();
        // DWORD flags = 1 (precise bounds set)
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        // FIXED preciseX = 10.5 (0x000A8000 in 16.16 format)
        baos.write(new byte[]{0x00, (byte) 0x80, 0x0A, 0x00});
        // FIXED preciseY = 20.25 (0x00144000 in 16.16 format, 20 * 65536 + 0.25 * 65536 = 1327104)
        baos.write(new byte[]{0x00, 0x40, 0x14, 0x00});
        // FIXED preciseWidth = 100.0 (0x00640000)
        baos.write(new byte[]{0x00, 0x00, 0x64, 0x00});
        // FIXED preciseHeight = 50.0 (0x00320000)
        baos.write(new byte[]{0x00, 0x00, 0x32, 0x00});
        // 16 bytes reserved
        baos.write(new byte[16]);

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        var chunk = CelExtraChunk.build(reader);

        assertThat(chunk.flags()).isEqualTo(1);
        assertThat(chunk.hasPreciseBounds()).isTrue();
        assertWithMessage("preciseX").that(chunk.preciseX()).isWithin(0.001).of(10.5);
        assertWithMessage("preciseY").that(chunk.preciseY()).isWithin(0.001).of(20.25);
        assertWithMessage("preciseWidth").that(chunk.preciseWidth()).isWithin(0.001).of(100.0);
        assertWithMessage("preciseHeight").that(chunk.preciseHeight()).isWithin(0.001).of(50.0);
    }

    @Test
    void readCelExtraWithoutPreciseBounds() throws IOException {
        var baos = new ByteArrayOutputStream();
        // DWORD flags = 0 (precise bounds NOT set)
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        // FIXED values (still read but not marked as valid)
        // preciseX = 0.0
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        // preciseY = 0.0
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        // preciseWidth = 0.0
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        // preciseHeight = 0.0
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        // 16 bytes reserved
        baos.write(new byte[16]);

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        var chunk = CelExtraChunk.build(reader);

        assertThat(chunk.flags()).isEqualTo(0);
        assertThat(chunk.hasPreciseBounds()).isFalse();
    }

    @Test
    void readCelExtraWithIntegerValues() throws IOException {
        var baos = new ByteArrayOutputStream();
        // DWORD flags = 1
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        // FIXED preciseX = 1.0 (0x00010000)
        baos.write(new byte[]{0x00, 0x00, 0x01, 0x00});
        // FIXED preciseY = 2.0 (0x00020000)
        baos.write(new byte[]{0x00, 0x00, 0x02, 0x00});
        // FIXED preciseWidth = 64.0 (0x00400000)
        baos.write(new byte[]{0x00, 0x00, 0x40, 0x00});
        // FIXED preciseHeight = 32.0 (0x00200000)
        baos.write(new byte[]{0x00, 0x00, 0x20, 0x00});
        // 16 bytes reserved
        baos.write(new byte[16]);

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        var chunk = CelExtraChunk.build(reader);

        assertWithMessage("preciseX").that(chunk.preciseX()).isWithin(0.0001).of(1.0);
        assertWithMessage("preciseY").that(chunk.preciseY()).isWithin(0.0001).of(2.0);
        assertWithMessage("preciseWidth").that(chunk.preciseWidth()).isWithin(0.0001).of(64.0);
        assertWithMessage("preciseHeight").that(chunk.preciseHeight()).isWithin(0.0001).of(32.0);
    }

    @Test
    void readCelExtraWithNegativeCoordinates() throws IOException {
        var baos = new ByteArrayOutputStream();
        // DWORD flags = 1
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        // FIXED preciseX = -5.0 (0xFFFB0000 in two's complement)
        baos.write(new byte[]{0x00, 0x00, (byte) 0xFB, (byte) 0xFF});
        // FIXED preciseY = -10.5 (0xFFF58000)
        baos.write(new byte[]{0x00, (byte) 0x80, (byte) 0xF5, (byte) 0xFF});
        // FIXED preciseWidth = 100.0 (0x00640000)
        baos.write(new byte[]{0x00, 0x00, 0x64, 0x00});
        // FIXED preciseHeight = 50.0 (0x00320000)
        baos.write(new byte[]{0x00, 0x00, 0x32, 0x00});
        // 16 bytes reserved
        baos.write(new byte[16]);

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        var chunk = CelExtraChunk.build(reader);

        assertWithMessage("preciseX").that(chunk.preciseX()).isWithin(0.001).of(-5.0);
        assertWithMessage("preciseY").that(chunk.preciseY()).isWithin(0.001).of(-10.5);
    }
}
