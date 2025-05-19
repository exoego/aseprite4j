package net.exoego.aseprite4j;

import java.io.IOException;

public final class UnknownChunk implements FrameChunk {
    private static final UnknownChunk INSTANCE = new UnknownChunk();

    static UnknownChunk build(InputStreamReader reader, int chunkSize) throws IOException {
        reader.skip(chunkSize);
        return INSTANCE;
    }
}
