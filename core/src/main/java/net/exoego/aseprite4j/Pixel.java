package net.exoego.aseprite4j;

public interface Pixel {
    record RGBA(short r, short g, short b, short a) implements Pixel {
    }

    record Grayscale(short value, short alpha) implements Pixel {
    }

    record Index(short index) implements Pixel {
    }
}
