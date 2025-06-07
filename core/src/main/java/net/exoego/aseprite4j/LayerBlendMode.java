package net.exoego.aseprite4j;

public enum LayerBlendMode {
    NORMAL(0),
    MULTIPLY(1),
    SCREEN(2),
    OVERLAY(3),
    DARKEN(4),
    LIGHTEN(5),
    COLOR_DODGE(6),
    COLOR_BURN(7),
    HARD_LIGHT(8),
    SOFT_LIGHT(9),
    DIFFERENCE(10),
    EXCLUSION(11),
    HUE(12),
    SATURATION(13),
    COLOR(14),
    LUMINOSITY(15),
    ADDITION(16),
    SUBTRACT(17),
    DIVIDE(18);

    private final int value;

    LayerBlendMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static LayerBlendMode from(int value) {
        return switch (value) {
            case 0 -> NORMAL;
            case 1 -> MULTIPLY;
            case 2 -> SCREEN;
            case 3 -> OVERLAY;
            case 4 -> DARKEN;
            case 5 -> LIGHTEN;
            case 6 -> COLOR_DODGE;
            case 7 -> COLOR_BURN;
            case 8 -> HARD_LIGHT;
            case 9 -> SOFT_LIGHT;
            case 10 -> DIFFERENCE;
            case 11 -> EXCLUSION;
            case 12 -> HUE;
            case 13 -> SATURATION;
            case 14 -> COLOR;
            case 15 -> LUMINOSITY;
            case 16 -> ADDITION;
            case 17 -> SUBTRACT;
            case 18 -> DIVIDE;
            default -> throw new IllegalArgumentException("Invalid layer blend mode value: " + value);
        };
    }
}
