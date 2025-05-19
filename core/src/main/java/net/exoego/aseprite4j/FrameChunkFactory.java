package net.exoego.aseprite4j;

import java.io.IOException;

class FrameChunkFactory {
    static FrameChunk read(InputStreamReader reader) throws IOException {
        var size = reader.DWORD();
        var type = reader.WORD();
        int chunkSize = Math.toIntExact(size - 6); // size includes type and size itself
        switch (type) {
            case 0x0004: // Old palette chunk
                break;
            case 0x0011: // Old palette chunk
                break;
            case 0x2004: // Layer Chunk
                break;
            case 0x2005: // Cel Chunk
                break;
            case 0x2006: // Cel Extra Chunk
                break;
            case 0x2007: // Color Profile Chunk
                break;
            case 0x2019: // Palette Chunk
                break;
            default:
                // ignore unknown type
                break;
        }
        reader.skip(chunkSize);
        return new FrameChunk() {
        };
    }
}
