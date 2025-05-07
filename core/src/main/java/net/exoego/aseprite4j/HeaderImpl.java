package net.exoego.aseprite4j;

import java.util.EnumSet;

final class HeaderImpl implements Header {
    long fileSize;
    int numFrames;
    int imageWidth, imageHeight;
    long flags;

    short transparentColorIndex;
    ColorDepth colorDepth;
    int numberOfColors;
    short pixelWidth, pixelHeight;
    int gridX, gridY;
    int gridWidth, gridHeight;

    @Override
    public long getFileSizeInBytes() {
        return fileSize;
    }

    @Override
    public int getNumberOfFrames() {
        return numFrames;
    }

    @Override
    public int getImageWidth() {
        return imageWidth;
    }

    @Override
    public int getImageHeight() {
        return imageHeight;
    }

    @Override
    public ColorDepth getColorDepth() {
        return colorDepth;
    }

    @Override
    public EnumSet<HeaderFlag> getFlagSet() {
        return HeaderFlag.setFrom(flags);
    }

    @Override
    public long getRawFlags() {
        return flags;
    }

    @Override
    public int getTransparentColorIndex() {
        return transparentColorIndex;
    }

    @Override
    public int getNumberOfColors() {
        return numberOfColors;
    }

    @Override
    public short getPixelWidth() {
        return pixelWidth;
    }

    @Override
    public short getPixelHeight() {
        return pixelHeight;
    }

    @Override
    public int getGridX() {
        return gridX;
    }

    @Override
    public int getGridY() {
        return gridY;
    }

    @Override
    public int getGridWidth() {
        return gridWidth;
    }

    @Override
    public int getGridHeight() {
        return gridHeight;
    }
}
