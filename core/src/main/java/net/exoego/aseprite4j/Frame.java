package net.exoego.aseprite4j;

public interface Frame {
    FrameHeader header();

    FrameChunk[] chunks();
}
