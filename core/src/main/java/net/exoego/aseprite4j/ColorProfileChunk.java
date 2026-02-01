package net.exoego.aseprite4j;

import java.io.IOException;

public record ColorProfileChunk(ColorProfile type, double gamma) implements FrameChunk {
    static ColorProfileChunk build(InputStreamReader reader) throws IOException {
        var type = reader.WORD();

        var flags = reader.WORD();
        if ((flags & 0x1) != 0) {
            // use special fixed gamma
            // what is this?
        }

        var gamma = reader.FIXED();

        var iccType = ColorProfile.from(type);

        // reserved
        reader.skip(8);

        switch (iccType) {
            case NoColorProfile -> {
            }
            case sRGB -> {
            }
            case EmbeddedICC -> {
                var iccProfileLength = reader.DWORD();
                // TODO: support icc profile?
                reader.skip(iccProfileLength);
            }
        }

        return new ColorProfileChunk(iccType, gamma);
    }
}
