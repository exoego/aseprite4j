package net.exoego.aseprite4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record PaletteChunk(int paletteSize, List<PaletteEntry> entries) implements FrameChunk {

    public record PaletteEntry(Color.RGBA color, Optional<String> name) {
    }

    static PaletteChunk build(InputStreamReader reader) throws IOException {
        var paletteSize = (int) reader.DWORD();
        var firstColorIndex = (int) reader.DWORD();
        var lastColorIndex = (int) reader.DWORD();
        // reserved
        reader.skip(8);

        var entries = new ArrayList<PaletteEntry>(lastColorIndex - firstColorIndex + 1);

        for (int i = firstColorIndex; i <= lastColorIndex; i++) {
            var flag = reader.WORD();
            boolean hasName = (flag & 0x1) > 0;

            var color = new Color.RGBA(reader.BYTE(), reader.BYTE(), reader.BYTE(), reader.BYTE());

            Optional<String> name = Optional.empty();
            if (hasName) {
                name = Optional.of(reader.STRING());
            }

            entries.add(new PaletteEntry(color, name));
        }

        return new PaletteChunk(paletteSize, entries);
    }
}
