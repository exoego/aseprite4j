package net.exoego.aseprite4j;

import java.util.EnumSet;

record HeaderImpl(
        long fileSizeInBytes,
        int numberOfFrames,
        int imageWidth,
        int imageHeight,
        long flagsRaw,
        int transparentColorIndex,
        ColorDepth colorDepth,
        int numberOfColors,
        short pixelWidth,
        short pixelHeight,
        int gridX,
        int gridY,
        int gridWidth,
        int gridHeight
) implements Header {
    @Override
    public EnumSet<HeaderFlag> flagsSet() {
        return HeaderFlag.setFrom(flagsRaw);
    }
}
