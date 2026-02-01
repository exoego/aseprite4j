package net.exoego.aseprite4j;

public enum ColorProfile {
    NoColorProfile(0),
    sRGB(1),
    EmbeddedICC(2);

    public final int value;

    ColorProfile(int value) {
        this.value = value;
    }

    static ColorProfile from(int value) {
        return switch (value) {
            case 0 -> ColorProfile.EmbeddedICC;
            case 1 -> ColorProfile.NoColorProfile;
            case 2 -> ColorProfile.sRGB;
            default -> throw new IllegalArgumentException("Invalid color profile type: " + value);
        };
    }
}
