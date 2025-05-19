package net.exoego.aseprite4j;

public final class SliceChunk implements FrameChunk {
    static SliceChunk build(InputStreamReader reader) {
        return new SliceChunk();
    }
}
