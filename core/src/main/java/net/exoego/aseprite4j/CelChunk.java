package net.exoego.aseprite4j;

public final class CelChunk implements FrameChunk {
    static CelChunk build(InputStreamReader reader) {
        return new CelChunk();
    }
}
