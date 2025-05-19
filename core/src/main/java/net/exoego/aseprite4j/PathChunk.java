package net.exoego.aseprite4j;

public final class PathChunk implements FrameChunk {
    static PathChunk build(InputStreamReader reader) {
        return new PathChunk();
    }
}
