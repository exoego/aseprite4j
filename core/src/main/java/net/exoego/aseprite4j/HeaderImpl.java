package net.exoego.aseprite4j;

final class HeaderImpl implements Header {
    long fileSize;
    int numFrames;
    int imageWidth, imageHeight;
    long flags;

    short transparentColorIndex;
    int colorDepth;
    int numberOfColors;
    short pixelWidth, pixelHeight;
    int gridX, gridY;
    int gridWidth, gridHeight;

    @Override
    public long getFileSize() {
        return fileSize;
    }

    @Override
    public int getFrames() {
        return numFrames;
    }

    @Override
    public int getWidth() {
        return imageWidth;
    }

    @Override
    public int getHeight() {
        return imageHeight;
    }

    @Override
    public int getColorDepth() {
        return colorDepth;
    }

    @Override
    public long getFlags() {
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
