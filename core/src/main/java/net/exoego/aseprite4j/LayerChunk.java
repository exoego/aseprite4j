package net.exoego.aseprite4j;

import java.io.IOException;
import java.util.OptionalInt;
import java.util.Set;

public record LayerChunk(
        int rawFlags,
        LayerType type,
        int childLevel,
        LayerBlendMode blendMode,
        short opacity,
        String name,
        OptionalInt tilesetIndex
) implements FrameChunk {
    static LayerChunk build(InputStreamReader reader) throws IOException {
        var flagsRaw = reader.WORD();
        var layerType = LayerType.from(reader.WORD());
        var childLevel = reader.WORD();

        // ignored
        var defaultLayerWidthInPixels = reader.WORD();
        // ignored
        var defaultLayerHeightInPixels = reader.WORD();

        var blendMode = LayerBlendMode.from(reader.WORD());
        var opacity = reader.BYTE();

        // future
        reader.skip(3);

        var layerName = reader.STRING();

        OptionalInt tilesetIndex = OptionalInt.empty();
        if (layerType == LayerType.TILEMAP) {
            tilesetIndex = OptionalInt.of((int) reader.DWORD());
        }

        return new LayerChunk(
                flagsRaw,
                layerType,
                childLevel,
                blendMode,
                opacity,
                layerName,
                tilesetIndex
        );
    }

    public Set<LayerFlag> flagsSet() {
        return LayerFlag.from(rawFlags);
    }
}
