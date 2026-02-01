package net.exoego.aseprite4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record SliceChunk(
        String name,
        long flags,
        List<SliceKey> keys
) implements FrameChunk {

    private static final long FLAG_9_PATCHES = 1;
    private static final long FLAG_HAS_PIVOT = 2;

    public boolean has9Patches() {
        return (flags & FLAG_9_PATCHES) != 0;
    }

    public boolean hasPivot() {
        return (flags & FLAG_HAS_PIVOT) != 0;
    }

    public record SliceKey(
            long frameNumber,
            int sliceXOrigin,
            int sliceYOrigin,
            int sliceWidth,
            int sliceHeight,
            Optional<NinePatchInfo> ninePatch,
            Optional<Point> pivot
    ) {
    }

    public record NinePatchInfo(
            int centerX,
            int centerY,
            int centerWidth,
            int centerHeight
    ) {
    }

    static SliceChunk build(InputStreamReader reader) throws IOException {
        int numberOfSliceKeys = (int) reader.DWORD();
        long flags = reader.DWORD();
        reader.DWORD(); // reserved

        String name = reader.STRING();

        boolean has9Patches = (flags & FLAG_9_PATCHES) != 0;
        boolean hasPivot = (flags & FLAG_HAS_PIVOT) != 0;

        List<SliceKey> keys = new ArrayList<>(numberOfSliceKeys);

        for (int i = 0; i < numberOfSliceKeys; i++) {
            long frameNumber = reader.DWORD();
            int sliceX = reader.LONG();
            int sliceY = reader.LONG();
            int width = (int) reader.DWORD();
            int height = (int) reader.DWORD();

            Optional<NinePatchInfo> ninePatch = Optional.empty();
            if (has9Patches) {
                int centerX = reader.LONG();
                int centerY = reader.LONG();
                int centerWidth = (int) reader.DWORD();
                int centerHeight = (int) reader.DWORD();
                ninePatch = Optional.of(new NinePatchInfo(centerX, centerY, centerWidth, centerHeight));
            }

            Optional<Point> pivot = Optional.empty();
            if (hasPivot) {
                int pivotX = reader.LONG();
                int pivotY = reader.LONG();
                pivot = Optional.of(new Point(pivotX, pivotY));
            }

            keys.add(new SliceKey(frameNumber, sliceX, sliceY, width, height, ninePatch, pivot));
        }

        return new SliceChunk(name, flags, keys);
    }
}
