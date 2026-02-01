package net.exoego.aseprite4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

record AsepriteFileImpl(Header header, List<Frame> frames) implements AsepriteFile {
    static AsepriteFile read(Path path, ReadOptions options) throws IOException {
        try (var in = java.nio.file.Files.newInputStream(path)) {
            var reader = new InputStreamReader(in, options.debugEnabled());

            var header = Header.read(reader);
            int numberOfFrames = header.numberOfFrames();
            var frames = new ArrayList<Frame>(numberOfFrames);
            for (int i = 0; i < numberOfFrames; i++) {
                var frame = Frame.read(reader, header.colorDepth());
                frames.add(frame);
            }
            return new AsepriteFileImpl(header, frames);
        }
    }

    @Override
    public String toString() {
        return "AsepriteFileImpl{" +
                "header=" + header +
                ", frames=[" + frames.stream().map(s -> s.toString()).collect(Collectors.joining(",\n")) +
                "]}";
    }
}
