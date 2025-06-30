package net.exoego.aseprite4j;

import java.io.IOException;
import java.util.Set;

public record TilesetChunk(long tilesetId, Set<TilesetFlag> tilesetFlagSet, long numberOfTiles, int tileWidth,
                           int tileHeight, int baseIndex, String name) implements FrameChunk {
    static TilesetChunk build(InputStreamReader reader, int chunkSize, ColorDepth colorDepth) throws IOException {
        // TODO: Implement
        var tilesetId = reader.DWORD();
        var tilsetFlags = TilesetFlag.from(reader.DWORD());
        var numberOfTiles = reader.DWORD();
        var tileWidth = reader.WORD();
        var tileHeight = reader.WORD();
        var baseIndex = reader.SHORT();


        // reserved
        reader.skip(14);

        var nameOfTileset = reader.STRING();

        System.out.println(
                "tilesetId: " + tilesetId +
                        ",\n tilsetFlags: " + tilsetFlags +
                        ",\n numberOfTiles: " + numberOfTiles +
                        ",\n tileWidth: " + tileWidth +
                        ",\n tileHeight: " + tileHeight +
                        ",\n baseIndex: " + baseIndex +
                        ",\n nameOfTileset: " + nameOfTileset
        );


        if (tilsetFlags.contains(TilesetFlag.INCLUDE_LINK_TO_EXTERNAL_FILE)) {
            var idOfExternalFile = reader.DWORD();
            var tilesetIdInExternalFile = reader.DWORD();
        }

        if (tilsetFlags.contains(TilesetFlag.INCLUDE_TILES_INSIDE_THIS_FILE)) {
            var dataLengthOfCompressedTilesetImage = reader.DWORD();
            int size = Math.toIntExact(tileWidth * tileHeight * numberOfTiles);
            System.out.println(
                    "dataLengthOfCompressedTilesetImage: " + dataLengthOfCompressedTilesetImage +
                            ",\n size: " + size
            );
            var compressedTilesetImage = reader.deflatePixels(Math.toIntExact(dataLengthOfCompressedTilesetImage),
                    size, colorDepth);
            System.out.println(
                    "compressedTilesetImage.length: " + compressedTilesetImage.length +
                            ",\n compressedTilesetImage: " + compressedTilesetImage
            );
        }

        if (tilsetFlags.contains(TilesetFlag.USE_TILE_ID_0_AS_EMPTY)) {
            // TODO
        }
        if (tilsetFlags.contains(TilesetFlag.X_FLIPPED_VERSION_WILL_BE_TRIED_TO_MATCH_WITH)) {
            // TODO
        }
        if (tilsetFlags.contains(TilesetFlag.Y_FLIPPED_VERSION_WILL_BE_TRIED_TO_MATCH_WITH)) {
            // TODO
        }
        if (tilsetFlags.contains(TilesetFlag.DIAGONAL_FLIPPED_VERSION_WILL_BE_TRIED_TO_MATCH_WITH)) {
            // TODO
        }

        return new TilesetChunk(tilesetId, tilsetFlags, numberOfTiles,
                tileWidth, tileHeight, baseIndex, nameOfTileset);
    }
}
