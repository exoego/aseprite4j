package net.exoego.aseprite4j;

import java.io.IOException;

record FrameHeaderImpl(long bytesInThisFrame, int frameDuration, long numberOfChunks) implements FrameHeader {
    static FrameHeader read(InputStreamReader reader) throws IOException {
        var bytesInThisFrame = reader.DWORD();
        var magicNumber = reader.WORD();
        if (magicNumber != 0xF1FA) {
            throw new IllegalArgumentException("Invalid frame header magic number: " + magicNumber);
        }
        var oldNumberOfChunks = reader.WORD();

        var frameDuration = reader.WORD();

        // For future (set to zero)
        reader.skip(2);

        var numberOfChunks = oldNumberOfChunks;
        if (oldNumberOfChunks == 0xFFFF) {
            numberOfChunks = (int) reader.DWORD();
        } else {
            reader.skip(4);
        }
        return new FrameHeaderImpl(bytesInThisFrame, frameDuration, numberOfChunks);
    }
}
