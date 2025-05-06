package net.exoego.aseprite4j;

import java.io.InputStream;
import java.nio.file.Path;

public interface Header {
    int MAGIC_NUMBER = 0xA5E0;

    long getFileSize();

    int getNumberOfFrames();

    int getImageWidth();

    int getImageHeight();

    ColorDepth getColorDepth();

    long getFlags();

    int getTransparentColorIndex();

    int getNumberOfColors();

    short getPixelWidth();

    short getPixelHeight();

    int getGridX();

    int getGridY();

    int getGridWidth();

    int getGridHeight();

    static Header read(Path path) {
        try (var in = java.nio.file.Files.newInputStream(path)) {
            return read(in);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static Header read(InputStream in) {
        var header = new HeaderImpl();
        var reader = new InputStreamReader(in);
        header.fileSize = reader.DWORD();
        var magicNumber = reader.WORD();
        if (magicNumber != MAGIC_NUMBER) {
            throw new IllegalArgumentException("Invalid magic number: " + Integer.toHexString(magicNumber));
        }
        header.numFrames = reader.WORD();
        header.imageWidth = reader.WORD();
        header.imageHeight = reader.WORD();
        header.colorDepth = ColorDepth.fromBitsPerPixel(reader.WORD());
        header.flags = reader.DWORD();

        // speed is DEPRECATED: You should use the frame duration field from each frame header
        reader.WORD();

        // Set be 0
        assert reader.WORD() == 0;

        // Set be 0
        assert reader.WORD() == 0;

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
        assert reader.skip(84) == 84;

        return header;
    }
}
