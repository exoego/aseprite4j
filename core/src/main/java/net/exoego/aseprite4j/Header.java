package net.exoego.aseprite4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.EnumSet;

/**
 * A 128-byte header
 */
public interface Header {
    /**
     * Magic number (0xA5E0) for Aseprite files.
     */
    int MAGIC_NUMBER = 0xA5E0;

    /**
     * @return Size of the file in bytes.
     */
    long fileSizeInBytes();

    /**
     * @return Number of frames of the image.
     */
    int numberOfFrames();

    /**
     * @return Width in pixels of the image.
     */
    int imageWidth();

    /**
     * @return Width in pixels of the image.
     */
    int imageHeight();

    /**
     * @return Color depth (bits per pixel) of the image.
     */
    ColorDepth colorDepth();

    /**
     * @return EnumSet of flags
     */
    EnumSet<HeaderFlag> flagsSet();

    /**
     * @return Raw value of flags
     */
    long flagsRaw();

    int transparentColorIndex();

    /**
     * @return Number of colors (0 means 256 for old sprites)
     */
    int numberOfColors();

    /**
     * @return the pixel width of the image.
     */
    short pixelWidth();

    /**
     * @return the pixel height of the image.
     */
    short pixelHeight();

    /**
     * @return pixel width/pixel height
     */
    default double getPixelAspectRatio() {
        if (pixelHeight() == 0) {
            return 1;
        }
        return (double) pixelWidth() / pixelHeight();
    }

    /**
     * @return the X position of the grid start
     */
    int gridX();

    /**
     * @return the Y position of the grid start
     */
    int gridY();

    /**
     * Grid width is 16px on Aseprite by default.
     *
     * @return Grid width (zero if there is no grid)
     */
    int gridWidth();

    /**
     * Grid height is 16px on Aseprite by default.
     *
     * @return Grid width (zero if there is no grid)
     */
    int gridHeight();

    static Header read(Path path) throws IOException {
        try (var in = java.nio.file.Files.newInputStream(path)) {
            return read(in);
        }
    }

    static Header read(InputStream in) throws IOException {
        var reader = new InputStreamReader(in);
        return read(reader);
    }

    static Header read(InputStreamReader reader) throws IOException {
        var fileSize = reader.DWORD();
        var magicNumber = reader.WORD();
        if (magicNumber != MAGIC_NUMBER) {
            throw new IllegalArgumentException("Invalid file header magic number: " + Integer.toHexString(magicNumber));
        }
        var numFrames = reader.WORD();
        var imageWidth = reader.WORD();
        var imageHeight = reader.WORD();
        var colorDepth = ColorDepth.from(reader.WORD());
        var rawFlags = reader.DWORD();

        // speed is DEPRECATED: You should use the frame duration field from each frame header
        reader.WORD();

        // Set be 0
        if (reader.DWORD() != 0) {
            throw new IllegalArgumentException("Invalid reserved field");
        }

        // Set be 0
        if (reader.DWORD() != 0) {
            throw new IllegalArgumentException("Invalid reserved field");
        }

        var transparentColorIndex = reader.BYTE();

        // Ignore these bytes
        reader.skip(3);

        var numberOfColors = reader.WORD();
        var pixelWidth = reader.BYTE();
        var pixelHeight = reader.BYTE();
        var gridX = reader.SHORT();
        var gridY = reader.SHORT();
        var gridWidth = reader.WORD();
        var gridHeight = reader.WORD();

        // For future (set to zero)
        reader.skip(84);

        return new HeaderImpl(
                fileSize,
                numFrames,
                imageWidth,
                imageHeight,
                rawFlags,
                transparentColorIndex,
                colorDepth,
                numberOfColors,
                pixelWidth,
                pixelHeight,
                gridX,
                gridY,
                gridWidth,
                gridHeight
        );
    }
}
