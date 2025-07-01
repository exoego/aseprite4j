package net.exoego.aseprite4j;

import java.io.IOException;

public interface CelChunk extends FrameChunk {
    int layerIndex();

    int xPosition();

    int yPosition();

    short opacityLevel();

    CelType celType();

    int zIndex();

    static CelChunk build(InputStreamReader reader, int wholeChunkSize, ColorDepth colorDepth) throws IOException {
        var layerIndex = reader.WORD(); // 2 bits
        var xPosition = reader.SHORT(); // 2 bits
        var yPosition = reader.SHORT(); // 2 bits
        var opacityLevel = reader.BYTE(); // 1 bit
        var celType = CelType.from(reader.WORD()); // 2 bits
        var zIndex = reader.SHORT(); // 2 bits

        // future
        reader.skip(5); // 5 bits

        var restOfChunkSize = wholeChunkSize - 16;
        System.out.println("wholeChunkSize: " + wholeChunkSize);
        System.out.println("restOfChunkSize: " + restOfChunkSize);

        try {
            System.out.println("cell 1");
            return switch (celType) {
                case RAW_IMAGE_DATA -> {
                    System.out.println("cel type: RAW_IMAGE_DATA");
                    var widthInPixels = reader.WORD();
                    var heightInPixels = reader.WORD();
                    var pixels = new Pixel[heightInPixels * widthInPixels];
                    for (int y = 0; y < heightInPixels; y++) {
                        for (int x = 0; x < widthInPixels; x++) {
                            var xy = y * widthInPixels + x;
                            pixels[xy] = reader.PIXEL(colorDepth);
                        }
                    }
                    yield new RawImageDataCelChunk(layerIndex, xPosition, yPosition, opacityLevel, celType, zIndex, widthInPixels, heightInPixels, pixels);
                }
                case LINKED_CEL -> {
                    System.out.println("cel type: LINKED_CEL");
                    var framePositionToLinkWith = reader.WORD();
                    yield new LinkedCelCelChunk(layerIndex, xPosition, yPosition, opacityLevel, celType, zIndex, framePositionToLinkWith);
                }
                case COMPRESSED_IMAGE -> {
                    System.out.println("cel type: COMPRESSED_IMAGE");
                    var widthInPixels = reader.WORD(); // 2 bits
                    var heightInPixels = reader.WORD(); // 2 bits
                    System.out.println("restOfChunkSize: " + (restOfChunkSize - 4 - 2));

                    var pixels = reader.deflatePixels(restOfChunkSize - 4,
                            heightInPixels * widthInPixels, colorDepth);
                    yield new CompressedImageCelChunk(layerIndex, xPosition, yPosition, opacityLevel, celType, zIndex, widthInPixels, heightInPixels, pixels);
                }
                case COMPRESSED_TILEMAP -> {
                    System.out.println("cel type: COMPRESSED_TILEMAP");
                    var widthInNumberOfTiles = reader.WORD(); // 2 bits
                    var heightInNumberOfTiles = reader.WORD(); // 2 bits
                    var bitsPerTile = reader.WORD(); // 2 bits

                    var bitmaskForTileId = reader.DWORD(); // 4 bits
                    var bitmaskFoxXFlip = reader.DWORD(); // 4 bits
                    var bitmaskForYFlip = reader.DWORD(); // 4 bits
                    var bitmaskForDiagonalFlip = reader.DWORD(); // 4 bits

                    // reserved
                    reader.skip(10); // 10 bits

                    var tileData = new Tile[heightInNumberOfTiles * widthInNumberOfTiles];

                    // TODO: decode
                    reader.skip(restOfChunkSize - 32);
//                reader.deflateZlib();
//                for (int y = 0; y < heightInNumberOfTiles; y++) {
//                    for (int x = 0; x < widthInNumberOfTiles; x++) {
//                        var xy = y * widthInNumberOfTiles + x;
//                        tileData[xy] = reader.TILE(bitsPerTile);
//                    }
//                }
                    System.out.println("cel read :" + reader.currentAddress());
                    yield new CompressedTilemapCelChunk(layerIndex, xPosition, yPosition, opacityLevel, celType, zIndex,
                            widthInNumberOfTiles, heightInNumberOfTiles, bitsPerTile, bitmaskForTileId, bitmaskFoxXFlip,
                            bitmaskForYFlip, bitmaskForDiagonalFlip, tileData);
                }
            };
        } finally {
            System.out.println("Cel chunk done: " + celType);
        }

    }

    record RawImageDataCelChunk(
            int layerIndex,
            int xPosition,
            int yPosition,
            short opacityLevel,
            CelType celType,
            int zIndex,
            int widthInPixels,
            int heightInPixels,
            Pixel[] pixelData
    ) implements CelChunk {
    }

    record LinkedCelCelChunk(
            int layerIndex,
            int xPosition,
            int yPosition,
            short opacityLevel,
            CelType celType,
            int zIndex,
            int framePositionToLinkWith
    ) implements CelChunk {
    }

    record CompressedImageCelChunk(
            int layerIndex,
            int xPosition,
            int yPosition,
            short opacityLevel,
            CelType celType,
            int zIndex,
            int widthInPixels,
            int heightInPixels,
            Pixel[] pixelData
    ) implements CelChunk {
    }

    record CompressedTilemapCelChunk(
            int layerIndex,
            int xPosition,
            int yPosition,
            short opacityLevel,
            CelType celType,
            int zIndex,
            int widthInNumberOfTiles,
            int heightInNumberOfTiles,
            int bitsPerTile,
            long bitmaskForTileId,
            long bitmaskFoxXFlip,
            long bitmaskForYFlip,
            long bitmaskForDiagonalFlip,
            Tile[] tileData
    ) implements CelChunk {
    }
}
