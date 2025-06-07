package net.exoego.aseprite4j;

import java.io.IOException;
import java.util.Set;

public record TilesetChunk(long tilesetId, Set<TilesetFlag> tilesetFlagSet, long numberOfTiles, int tileWidth, int tileHeight, int baseIndex, String name) implements FrameChunk {
    static TilesetChunk build(InputStreamReader reader, ColorDepth colorDepth) throws IOException {
        // TODO: Implement
        var tilesetId = reader.DWORD();
        var tilsetFlags = TilesetFlag.from(reader.DWORD());
        var numberOfTiles = reader.DWORD();
        var tileWidth = reader.WORD();
        var tileHeight = reader.WORD();
        var baseIndex = reader.SHORT();

        // reserved
        reader.skip(4);

        var nameOfTileset = reader.STRING();

        if (tilsetFlags.contains(TilesetFlag.INCLUDE_LINK_TO_EXTERNAL_FILE)) {
            var idOfExternalFile = reader.DWORD();
            var tilesetIdInExternalFile = reader.DWORD();
        }

        if (tilsetFlags.contains(TilesetFlag.INCLUDE_TILES_INSIDE_THIS_FILE)) {
            var dataLengthOfCompressedTilesetImage = reader.DWORD();
            int size = (int) (tileWidth * tileHeight * numberOfTiles);
            var compressedTilesetImage = new Pixel[size];
            for (int i = 0; i < size; i++) {
                compressedTilesetImage[i] = reader.PIXEL(colorDepth);
            }
        }

        return new TilesetChunk(tilesetId, tilsetFlags, numberOfTiles,
                tileWidth, tileHeight, baseIndex, nameOfTileset);
    }
}
