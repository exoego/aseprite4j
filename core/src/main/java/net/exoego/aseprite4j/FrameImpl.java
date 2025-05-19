package net.exoego.aseprite4j;

import java.io.IOException;

record FrameImpl(FrameHeader header, FrameChunk[] chunks) implements Frame {
    static Frame read(InputStreamReader reader) throws IOException {
        var header = FrameHeaderImpl.read(reader);

        final int lenExact = Math.toIntExact(header.getNumberOfChunks());
        var chunks = new FrameChunk[lenExact];
        for (int i = 0; i < lenExact; i++) {
            chunks[i] = FrameChunkFactory.read(reader);
        }

        return new FrameImpl(header, chunks);
    }
}
