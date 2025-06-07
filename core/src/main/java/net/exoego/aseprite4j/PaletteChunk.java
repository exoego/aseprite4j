package net.exoego.aseprite4j;

import java.io.IOException;
import java.util.List;

public record PaletteChunk(List<Color.RGBA> palette) implements FrameChunk {
    static PaletteChunk build(InputStreamReader reader) throws IOException {
        var paletteSize = (int) reader.DWORD();
        var firstColorIndex = (int) reader.DWORD();
        var lastColorIndex = (int) reader.DWORD();
        // reserved
        reader.skip(8);


        var palette = new Color.RGBA[paletteSize];

        var from = firstColorIndex;
        var to = lastColorIndex;
        var range = to - from + 1;

        for (int i = from; i < range; i++) {
            var flag = reader.WORD();
            boolean hasName = (flag & 0x1) > 0;

            var color = new Color.RGBA(reader.BYTE(), reader.BYTE(), reader.BYTE(), reader.BYTE());
            if (hasName) {
                // TODO: name is not used
                reader.STRING();
            }
            palette[i] = color;
        }

        return new PaletteChunk(List.of(palette));
    }
}
