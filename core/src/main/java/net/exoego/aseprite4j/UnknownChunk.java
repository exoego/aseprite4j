package net.exoego.aseprite4j;

import java.io.IOException;

final class UnknownChunk implements FrameChunk {
    private static final UnknownChunk INSTANCE = new UnknownChunk();

    static UnknownChunk build(InputStreamReader reader, int type, int chunkSize) throws IOException {
        try {
            // TODO: warn about unknown chunk type
            reader.skip(chunkSize);
            return INSTANCE;
        } catch (IOException e) {
            System.err.println("Failed to read unknown chunk type: " + type + ", size: " + chunkSize);
            throw e;
        }
    }
}
