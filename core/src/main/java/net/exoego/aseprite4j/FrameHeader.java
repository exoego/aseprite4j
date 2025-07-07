package net.exoego.aseprite4j;

public interface FrameHeader {
    long bytesInThisFrame();

    int frameDuration();

    long numberOfChunks();
}
