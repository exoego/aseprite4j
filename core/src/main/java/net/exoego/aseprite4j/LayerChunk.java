package net.exoego.aseprite4j;

public final class LayerChunk implements FrameChunk {
    static LayerChunk build(InputStreamReader reader) {
        return new LayerChunk();
    }
}
