package net.exoego.aseprite4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

record Frame(FrameHeader header, List<FrameChunk> chunks) {
    static Frame read(InputStreamReader reader, ColorDepth colorDepth) throws IOException {
        var header = FrameHeaderImpl.read(reader);

        final int lenExact = Math.toIntExact(header.numberOfChunks());
        var chunks = new ArrayList<FrameChunk>(lenExact);
        try {
            for (int i = 0; i < lenExact; i++) {
                chunks.add(FrameChunkFactory.read(reader, colorDepth));
            }
        } catch (IOException e) {
            System.err.println("header: " + header.toString());
            System.err.println("chunk ここまで読めた: " + chunks.size());
            for (FrameChunk chunk : chunks) {
                System.err.println("chunk: " + chunk);
            }
            throw e;
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
