package net.exoego.aseprite4j;

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
    long getFileSizeInBytes();

    /**
     * @return Number of frames of the image.
     */
    int getNumberOfFrames();

    /**
     * @return Width in pixels of the image.
     */
    int getImageWidth();

    /**
     * @return Width in pixels of the image.
     */
    int getImageHeight();

    /**
     * @return Color depth (bits per pixel) of the image.
     */
    ColorDepth getColorDepth();

    /**
     * @return EnumSet of flags
     */
    EnumSet<HeaderFlag> getFlagSet();

    /**
     * @return Raw value of flags
     */
    long getRawFlags();

    int getTransparentColorIndex();

    /**
     * @return Number of colors (0 means 256 for old sprites)
     */
    int getNumberOfColors();

    /**
     * @return the pixel width of the image.
     */
    short getPixelWidth();

    /**
     * @return the pixel height of the image.
     */
    short getPixelHeight();

    /**
     * @return pixel width/pixel height
     */
    default double getPixelAspectRatio() {
        if (getPixelHeight() == 0) {
            return 1;
        }
        return (double) getPixelWidth() / getPixelHeight();
    }

    /**
     * @return the X position of the grid start
     */
    int getGridX();

    /**
     * @return the Y position of the grid start
     */
    int getGridY();

    /**
     * Grid width is 16px on Aseprite by default.
     *
     * @return Grid width (zero if there is no grid)
     */
    int getGridWidth();

    /**
     * Grid height is 16px on Aseprite by default.
     *
     * @return Grid width (zero if there is no grid)
     */
    int getGridHeight();

    static Header read(Path path) {
        try (var in = java.nio.file.Files.newInputStream(path)) {
            return read(in);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static Header read(InputStream in) {
        var reader = new InputStreamReader(in);
        return read(reader);
    }

    static Header read(InputStreamReader reader) {
        var header = new HeaderImpl();
        header.fileSize = reader.DWORD();
        var magicNumber = reader.WORD();
        if (magicNumber != MAGIC_NUMBER) {
            throw new IllegalArgumentException("Invalid magic number: " + Integer.toHexString(magicNumber));
        }
        header.numFrames = reader.WORD();
        header.imageWidth = reader.WORD();
        header.imageHeight = reader.WORD();
        header.colorDepth = ColorDepth.from(reader.WORD());
        header.flags = reader.DWORD();

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

        header.transparentColorIndex = reader.BYTE();

        // Ignore these bytes
        reader.skip(3);

        header.numberOfColors = reader.WORD();
        header.pixelWidth = reader.BYTE();
        header.pixelHeight = reader.BYTE();
        header.gridX = reader.SHORT();
        header.gridY = reader.SHORT();
        header.gridWidth = reader.WORD();
        header.gridHeight = reader.WORD();

        // For future (set to zero)
        reader.skip(84);

        return header;
    }
}
