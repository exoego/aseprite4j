package net.exoego.aseprite4j;

import java.io.IOException;

class FrameChunkFactory {
    static FrameChunk read(InputStreamReader reader, ColorDepth colorDepth) throws IOException {
        var size = reader.DWORD();
        var type = reader.WORD();
        int chunkSize = Math.max(Math.toIntExact(size - 6), 0); // size includes type and size itself

        System.out.println(
                String.format("exact chunk size: %d for type %s", +chunkSize, chunkTypeName(type)));

        try {
            return switch (type) {
                case 0x0004 -> OldPaletteChunk4.build(reader);
                case 0x0011 -> OldPaletteChunk11.build(reader);
                case 0x2004 -> LayerChunk.build(reader);
                case 0x2005 -> CelChunk.build(reader, chunkSize, colorDepth);
                case 0x2006 -> CelExtraChunk.build(reader);
                case 0x2007 -> ColorProfileChunk.build(reader);
                case 0x2008 -> ExternalFilesChunk.build(reader);
                case 0x2016 -> MaskChunk.build(reader);
                case 0x2017 -> PathChunk.build(reader);
                case 0x2018 -> TagsChunk.build(reader);
                case 0x2019 -> PaletteChunk.build(reader);
                case 0x2020 -> UserDataChunk.build(chunkSize, reader);
                case 0x2022 -> SliceChunk.build(reader);
                case 0x2023 -> TilesetChunk.build(reader, chunkSize, colorDepth);
                default -> UnknownChunk.build(reader, type, chunkSize);
            };
        } finally {
            System.out.println(reader.currentAddress());
        }
    }

    private static String chunkTypeName(int type) {
        return switch (type) {
            case 0x0004 -> "OldPaletteChunk4";
            case 0x0011 -> "OldPaletteChunk11";
            case 0x2004 -> "LayerChunk";
            case 0x2005 -> "CelChunk";
            case 0x2006 -> "CelExtraChunk";
            case 0x2007 -> "ColorProfileChunk";
            case 0x2008 -> "ExternalFilesChunk";
            case 0x2016 -> "MaskChunk";
            case 0x2017 -> "PathChunk";
            case 0x2018 -> "TagsChunk";
            case 0x2019 -> "PaletteChunk";
            case 0x2020 -> "UserDataChunk";
            case 0x2022 -> "SliceChunk";
            case 0x2023 -> "TilesetChunk";
            default -> "UnknownChunk";
        };
    }

}
