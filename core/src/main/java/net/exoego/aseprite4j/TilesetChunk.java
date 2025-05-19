package net.exoego.aseprite4j;

public final class TilesetChunk implements FrameChunk {
    static TilesetChunk build(InputStreamReader reader) {
        return new TilesetChunk();
    }
}
