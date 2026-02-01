package net.exoego.aseprite4j;

import java.io.IOException;

/**
 * Mask Chunk (0x2016) - DEPRECATED
 * This chunk is deprecated and should not be used in new files.
 * It's kept for backward compatibility with old .aseprite files.
 */
public record MaskChunk(
        int x,
        int y,
        int width,
        int height,
        String name,
        byte[] bitmapData
) implements FrameChunk {

    static MaskChunk build(InputStreamReader reader) throws IOException {
        int x = reader.SHORT();
        int y = reader.SHORT();
        int width = reader.WORD();
        int height = reader.WORD();
        reader.skip(8); // reserved (for future use)

        String name = reader.STRING();

        // Bitmap data: each bit represents a pixel (1 = masked, 0 = not masked)
        // Row size in bytes = (width + 7) / 8
        int rowBytes = (width + 7) / 8;
        int bitmapSize = height * rowBytes;
        byte[] bitmapData = reader.readNBytes(bitmapSize);

        return new MaskChunk(x, y, width, height, name, bitmapData);
    }
}
