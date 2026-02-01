package net.exoego.aseprite4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public record Frame(FrameHeader header, List<FrameChunk> chunks) {
    static Frame read(InputStreamReader reader, ColorDepth colorDepth) throws IOException {
        var header = FrameHeaderImpl.read(reader);

        final int lenExact = Math.toIntExact(header.numberOfChunks());
        var chunks = new ArrayList<FrameChunk>(lenExact);
        for (int i = 0; i < lenExact; i++) {
            chunks.add(FrameChunkFactory.read(reader, colorDepth));
        }

        return new Frame(header, chunks);
    }

    @Override
    public String toString() {
        return "Frame{" +
                "header=" + header +
                ",\n chunks=[\n" + chunks.stream().map(s -> "    " + s).collect(java.util.stream.Collectors.joining(",\n")) +
                "]}";
    }
}
