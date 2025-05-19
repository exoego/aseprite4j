package net.exoego.aseprite4j;

public enum ColorProfile {
    NoColorProfile(0),
    sRGB(1),
    EmbeddedICC(2);

    public final int value;

    ColorProfile(int value) {
        this.value = value;
    }
}
