package net.exoego.aseprite4j;

public enum ColorDepth {
    RGBA(32),
    Grayscale(16),
    Indexed(8);

    private final int bitsPerPixel;

    ColorDepth(int bitsPerPixel) {
        this.bitsPerPixel = bitsPerPixel;
    }

    public int getBitsPerPixel() {
        return bitsPerPixel;
    }

    public static ColorDepth from(int bitsPerPixel) {
        return switch (bitsPerPixel) {
            case 32 -> RGBA;
            case 16 -> Grayscale;
            case 8 -> Indexed;
            default ->
                    throw new IllegalArgumentException("No ColorDepth found for " + bitsPerPixel + " bits per pixel");
        };
    }
}
