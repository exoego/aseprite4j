package net.exoego.aseprite4j;

public final class MaskChunk implements FrameChunk {
    static MaskChunk build(InputStreamReader reader) {
        return new MaskChunk();
    }
}
