package net.exoego.aseprite4j;

public final class CelExtraChunk implements FrameChunk {
    static CelExtraChunk build(InputStreamReader reader) {
        return new CelExtraChunk();
    }
}
