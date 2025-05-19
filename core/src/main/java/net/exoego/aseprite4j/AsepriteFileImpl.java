package net.exoego.aseprite4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

record AsepriteFileImpl(Header header, List<Frame> frames) implements AsepriteFile {
    static AsepriteFile read(Path path) throws IOException {
        try (var in = java.nio.file.Files.newInputStream(path)) {
            var reader = new InputStreamReader(in);

            var header = Header.read(reader);
            int numberOfFrames = header.getNumberOfFrames();
            var frames = new ArrayList<Frame>(numberOfFrames);
            for (int i = 0; i < numberOfFrames; i++) {
                var frame = FrameImpl.read(reader);
                frames.add(frame);
            }
            return new AsepriteFileImpl(header, frames);
        }
    }
}
