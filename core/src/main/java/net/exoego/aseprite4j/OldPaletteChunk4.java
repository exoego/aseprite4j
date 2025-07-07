package net.exoego.aseprite4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public record OldPaletteChunk4(List<Color.RGB> palette) implements FrameChunk {
    // TODO: Implement
    static OldPaletteChunk4 build(InputStreamReader reader) throws IOException {
        var numberOfPackets = reader.WORD();
        var palette = new ArrayList<Color.RGB>(numberOfPackets);
        for (int i = 0; i < numberOfPackets; i++) {
            var numberOfPaletteEntriesToSkipFromLastPacket = reader.BYTE();
            var numberOfColorsInPalette = reader.BYTE();
            for (int j = 0; j < numberOfColorsInPalette; j++) {
                palette.add(new Color.RGB(reader.BYTE(), reader.BYTE(), reader.BYTE()));
            }
            reader.skip(numberOfPaletteEntriesToSkipFromLastPacket);
        }
        System.out.println("OldPaletteChunk4: " + palette.size() + " colors read");
        return new OldPaletteChunk4(palette);
    }
}
