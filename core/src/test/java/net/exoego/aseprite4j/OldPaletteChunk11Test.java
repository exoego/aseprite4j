package net.exoego.aseprite4j;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;

public class OldPaletteChunk11Test {

    @Test
    void readSinglePacketWithOneColor() throws IOException {
        // WORD: 1 packet
        // BYTE: 0 entries to skip
        // BYTE: 1 color
        // RGB: (63, 32, 0) in 0-63 range
        var bytes = new byte[]{
                0x01, 0x00,  // 1 packet
                0x00,        // 0 entries to skip
                0x01,        // 1 color
                0x3F, 0x20, 0x00  // RGB (63, 32, 0)
        };
        var reader = new InputStreamReader(new ByteArrayInputStream(bytes));
        var chunk = OldPaletteChunk11.build(reader);

        assertThat(chunk.palette()).hasSize(1);
        var color = chunk.palette().get(0);
        // 63 -> 255, 32 -> 130, 0 -> 0
        assertThat(color.r()).isEqualTo((short) 255);
        assertThat(color.g()).isEqualTo((short) 130);
        assertThat(color.b()).isEqualTo((short) 0);
    }

    @Test
    void readMultiplePackets() throws IOException {
        // 2 packets
        var bytes = new byte[]{
                0x02, 0x00,  // 2 packets
                // Packet 1
                0x00,        // 0 entries to skip
                0x02,        // 2 colors
                0x00, 0x00, 0x00,  // black
                0x3F, 0x3F, 0x3F,  // white (63,63,63)
                // Packet 2
                0x00,        // 0 entries to skip
                0x01,        // 1 color
                0x1F, 0x1F, 0x1F   // gray (31,31,31)
        };
        var reader = new InputStreamReader(new ByteArrayInputStream(bytes));
        var chunk = OldPaletteChunk11.build(reader);

        assertThat(chunk.palette()).hasSize(3);
        // Black
        assertThat(chunk.palette().get(0).r()).isEqualTo((short) 0);
        // White (63 -> 255)
        assertThat(chunk.palette().get(1).r()).isEqualTo((short) 255);
        // Gray (31 -> 125 using bit shift: (31 << 2) | (31 >> 4) = 124 | 1 = 125)
        assertThat(chunk.palette().get(2).r()).isEqualTo((short) 125);
    }

    @Test
    void colorScalingIsCorrect() throws IOException {
        // Test boundary values
        var bytes = new byte[]{
                0x01, 0x00,  // 1 packet
                0x00,        // 0 entries to skip
                0x03,        // 3 colors
                0x00, 0x00, 0x00,  // 0 -> 0
                0x20, 0x20, 0x20,  // 32 -> 130
                0x3F, 0x3F, 0x3F   // 63 -> 255
        };
        var reader = new InputStreamReader(new ByteArrayInputStream(bytes));
        var chunk = OldPaletteChunk11.build(reader);

        assertThat(chunk.palette()).hasSize(3);
        assertThat(chunk.palette().get(0).r()).isEqualTo((short) 0);
        assertThat(chunk.palette().get(1).r()).isEqualTo((short) 130);
        assertThat(chunk.palette().get(2).r()).isEqualTo((short) 255);
    }
}
