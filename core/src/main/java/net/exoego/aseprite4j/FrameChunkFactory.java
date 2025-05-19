package net.exoego.aseprite4j;

import java.io.IOException;

class FrameChunkFactory {
    static FrameChunk read(InputStreamReader reader) throws IOException {
        var size = reader.DWORD();
        var type = reader.WORD();
        int chunkSize = Math.toIntExact(size - 6); // size includes type and size itself

        return switch (type) {
            case 0x0004 -> OldPaletteChunk4.build(reader);
            case 0x0011 -> OldPaletteChunk11.build(reader);
            case 0x2004 -> LayerChunk.build(reader);
            case 0x2005 -> CelChunk.build(reader);
            case 0x2006 -> CelExtraChunk.build(reader);
            case 0x2007 -> ColorProfileChunk.build(reader);
            case 0x2008 -> ExternalFilesChunk.build(reader);
            case 0x2016 -> MaskChunk.build(reader);
            case 0x2017 -> PathChunk.build(reader);
            case 0x2018 -> TagsChunk.build(reader);
            case 0x2019 -> PaletteChunk.build(reader);
            case 0x2020 -> UserDataChunk.build(reader);
            case 0x2022 -> SliceChunk.build(reader);
            case 0x2023 -> TilesetChunk.build(reader);
            default -> UnknownChunk.build(reader, type, chunkSize);
        };
    }
}
