package net.exoego.aseprite4j;

public final class ColorProfileChunk implements FrameChunk {
    static ColorProfileChunk build(InputStreamReader reader) {
        return new ColorProfileChunk();
    }
}
