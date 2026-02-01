package net.exoego.aseprite4j;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public record TilesetChunk(
        long tilesetId,
        Set<TilesetFlag> tilesetFlagSet,
        long numberOfTiles,
        int tileWidth,
        int tileHeight,
        int baseIndex,
        String name,
        Optional<ExternalFileReference> externalFileReference,
        Optional<Pixel[]> tilesetImage
) implements FrameChunk {

    public record ExternalFileReference(long externalFileId, long tilesetIdInExternalFile) {
    }

    static TilesetChunk build(InputStreamReader reader, int chunkSize, ColorDepth colorDepth) throws IOException {
        var tilesetId = reader.DWORD();
        var tilesetFlags = TilesetFlag.from(reader.DWORD());
        var numberOfTiles = reader.DWORD();
        var tileWidth = reader.WORD();
        var tileHeight = reader.WORD();
        var baseIndex = reader.SHORT();

        // reserved
        reader.skip(14);

        var nameOfTileset = reader.STRING();

        Optional<ExternalFileReference> externalFileReference = Optional.empty();
        if (tilesetFlags.contains(TilesetFlag.INCLUDE_LINK_TO_EXTERNAL_FILE)) {
            var idOfExternalFile = reader.DWORD();
            var tilesetIdInExternalFile = reader.DWORD();
            externalFileReference = Optional.of(new ExternalFileReference(idOfExternalFile, tilesetIdInExternalFile));
        }

        Optional<Pixel[]> tilesetImage = Optional.empty();
        if (tilesetFlags.contains(TilesetFlag.INCLUDE_TILES_INSIDE_THIS_FILE)) {
            var dataLengthOfCompressedTilesetImage = reader.DWORD();
            int size = Math.toIntExact(tileWidth * tileHeight * numberOfTiles);
            var compressedTilesetImageReader = reader.decompressZlib(Math.toIntExact(dataLengthOfCompressedTilesetImage));
            var pixels = compressedTilesetImageReader.PIXELS(size, colorDepth);
            tilesetImage = Optional.of(pixels);
        }

        return new TilesetChunk(
                tilesetId,
                tilesetFlags,
                numberOfTiles,
                tileWidth,
                tileHeight,
                baseIndex,
                nameOfTileset,
                externalFileReference,
                tilesetImage
        );
    }
}
