package net.exoego.aseprite4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface AsepriteFile {
    Header header();

    List<Frame> frames();

    static AsepriteFile read(Path path) throws IOException {
        return AsepriteFileImpl.read(path, ReadOptions.DEFAULT);
    }

    static AsepriteFile read(Path path, ReadOptions options) throws IOException {
        return AsepriteFileImpl.read(path, options);
    }

    record ReadOptions(boolean debugEnabled) {
        public static final ReadOptions DEFAULT = new ReadOptions(false);
        public static final ReadOptions DEBUG = new ReadOptions(true);
    }
}
