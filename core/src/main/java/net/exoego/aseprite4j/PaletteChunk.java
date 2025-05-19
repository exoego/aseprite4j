package net.exoego.aseprite4j;

public final class PaletteChunk implements FrameChunk {
    static PaletteChunk build(InputStreamReader reader) {
        return new PaletteChunk();
    }
}
