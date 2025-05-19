package net.exoego.aseprite4j;

import java.io.IOException;

public record ColorProfileChunk(ColorProfile type, double gamma) implements FrameChunk {
    static ColorProfileChunk build(InputStreamReader reader) throws IOException {
        var type = reader.WORD();
        // what is this?
        var flags = reader.WORD();
        var gamma = reader.FIXED();

        var iccType = switch (type) {
            case 0 -> ColorProfile.EmbeddedICC;
            case 1 -> ColorProfile.NoColorProfile;
            case 2 -> ColorProfile.sRGB;
            default -> throw new IllegalArgumentException("Invalid color profile type: " + type);
        };

        reader.skip(8); // reserved

        if (type == ColorProfile.EmbeddedICC.value) {
            var iccProfileLength = reader.DWORD();
            // TODO: support icc profile?
            reader.skip((int) iccProfileLength);
        }

        return new ColorProfileChunk(iccType, gamma);
    }
}
