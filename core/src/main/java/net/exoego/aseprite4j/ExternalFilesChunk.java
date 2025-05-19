package net.exoego.aseprite4j;

public final class ExternalFilesChunk implements FrameChunk {
    static ExternalFilesChunk build(InputStreamReader reader) {
        return new ExternalFilesChunk();
    }
}
