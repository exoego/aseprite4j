package net.exoego.aseprite4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Old Palette Chunk (0x0011) - DEPRECATED
 * Old palette chunk format. Colors are stored in 0-63 range and should be
 * scaled to 0-255 range when used.
 */
public record OldPaletteChunk11(List<Color.RGB> palette) implements FrameChunk {

    static OldPaletteChunk11 build(InputStreamReader reader) throws IOException {
        int numberOfPackets = reader.WORD();
        var palette = new ArrayList<Color.RGB>();

        for (int i = 0; i < numberOfPackets; i++) {
            // Skip entries in palette
            int entriesToSkip = reader.BYTE();
            int numberOfColors = reader.BYTE();

            // If numberOfColors is 0, it means 256 colors
            if (numberOfColors == 0) {
                numberOfColors = 256;
            }

            for (int j = 0; j < numberOfColors; j++) {
                // Colors are in 0-63 range, scale to 0-255
                int r = reader.BYTE();
                int g = reader.BYTE();
                int b = reader.BYTE();

                // Scale from 0-63 to 0-255: multiply by 255/63 ≈ 4.047619
                // Common approach: (value * 255 + 31) / 63 for proper rounding
                // Or simpler: value << 2 | value >> 4
                short scaledR = scaleColor(r);
                short scaledG = scaleColor(g);
                short scaledB = scaleColor(b);

                palette.add(new Color.RGB(scaledR, scaledG, scaledB));
            }
        }

        return new OldPaletteChunk11(palette);
    }

    private static short scaleColor(int value) {
        // Scale from 0-63 to 0-255
        // Using bit manipulation: (value << 2) | (value >> 4)
        // This maps 0->0, 63->255 correctly
        return (short) ((value << 2) | (value >> 4));
    }
}
