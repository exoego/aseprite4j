package net.exoego.aseprite4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public record ExternalFilesChunk(List<Entry> entries) implements FrameChunk {
    public record Entry(int entryId, Type entryType, String externalFileName) {
        public enum Type {
            EXTERNAL_PALETTE,
            EXTERNAL_TILESET,
            EXTENSION_NAME_FOR_PROPERTIES,
            EXTENSION_NAME_FOR_TILE_MANAGEMENT;

            static Type from(int type) {
                return switch (type) {
                    case 0x00 -> EXTERNAL_PALETTE;
                    case 0x01 -> EXTERNAL_TILESET;
                    case 0x02 -> EXTENSION_NAME_FOR_PROPERTIES;
                    case 0x03 -> EXTENSION_NAME_FOR_TILE_MANAGEMENT;
                    default -> throw new IllegalArgumentException("Unknown entry type: " + type);
                };
            }
        }
    }

    static ExternalFilesChunk build(InputStreamReader reader) throws IOException {
        var numberOfEntries = reader.DWORD();
        reader.skip(8); // reserved

        var entries = new ArrayList<Entry>((int) numberOfEntries);

        for (int i = 0; i < numberOfEntries; i++) {
            var entryId = reader.DWORD();
            var entryType = Entry.Type.from(reader.BYTE());
            reader.skip(7); // reserved
            var externalFileName = reader.STRING();
            entries.add(new Entry((int) entryId, entryType, externalFileName));
        }
        return new ExternalFilesChunk(entries);
    }
}
