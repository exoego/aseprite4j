package net.exoego.aseprite4j;

import java.io.IOException;

public record CelExtraChunk(
        long flags,
        double preciseX,
        double preciseY,
        double preciseWidth,
        double preciseHeight
) implements FrameChunk {

    private static final long FLAG_PRECISE_BOUNDS_SET = 1;

    public boolean hasPreciseBounds() {
        return (flags & FLAG_PRECISE_BOUNDS_SET) != 0;
    }

    static CelExtraChunk build(InputStreamReader reader) throws IOException {
        long flags = reader.DWORD();
        double preciseX = reader.FIXED();
        double preciseY = reader.FIXED();
        double preciseWidth = reader.FIXED();
        double preciseHeight = reader.FIXED();
        reader.skip(16); // reserved (future use)

        return new CelExtraChunk(flags, preciseX, preciseY, preciseWidth, preciseHeight);
    }
}
