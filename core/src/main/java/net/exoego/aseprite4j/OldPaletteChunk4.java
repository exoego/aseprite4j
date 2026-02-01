package net.exoego.aseprite4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public record OldPaletteChunk4(List<Color.RGB> palette) implements FrameChunk {
    static OldPaletteChunk4 build(InputStreamReader reader) throws IOException {
        var numberOfPackets = reader.WORD();
        var palette = new ArrayList<Color.RGB>();

        for (int i = 0; i < numberOfPackets; i++) {
            // Number of palette entries to skip from the last packet (affects index, not bytes)
            var entriesToSkip = reader.BYTE();
            var numberOfColors = reader.BYTE();

            // If numberOfColors is 0, it means 256 colors
            if (numberOfColors == 0) {
                numberOfColors = 256;
            }

            for (int j = 0; j < numberOfColors; j++) {
                palette.add(new Color.RGB(reader.BYTE(), reader.BYTE(), reader.BYTE()));
            }
        }
        return new OldPaletteChunk4(palette);
    }
}
