package net.exoego.aseprite4j;

public final class UserDataChunk implements FrameChunk {
    static UserDataChunk build(InputStreamReader reader) {
        return new UserDataChunk();
    }
}
