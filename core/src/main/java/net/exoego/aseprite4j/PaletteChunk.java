package net.exoego.aseprite4j;

import java.io.IOException;

public final class PaletteChunk implements FrameChunk {
    static PaletteChunk build(InputStreamReader reader) throws IOException {
        var paletteSize = (int) reader.DWORD();
        var firstColorIndex = (int) reader.DWORD();
        var lastColorIndex = (int) reader.DWORD();

        var palette = new Color[paletteSize];

        // reserved
        reader.skip(8);

        for (int i = firstColorIndex; i <= lastColorIndex; i++) {
            var flag = reader.WORD();
            boolean hasName = (flag & 0x1) > 0;

            var color = new Color(reader.BYTE(), reader.BYTE(), reader.BYTE());
            if (hasName) {
                // TODO: name is not used
                reader.STRING();
            }
            palette[i] = color;
        }

        throw new UnsupportedOperationException();
    }
}
