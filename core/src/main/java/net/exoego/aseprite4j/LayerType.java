package net.exoego.aseprite4j;

public enum LayerType {
    NORMAL(0), GROUP(1), TILEMAP(2);

    private final int value;

    LayerType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    static  LayerType from(int value) {
        return switch (value) {
            case 0 -> LayerType.NORMAL;
            case 1 -> LayerType.GROUP;
            case 2 -> LayerType.TILEMAP;
            default -> throw new IllegalArgumentException("Invalid layer type: " + value);
        };
    }
}
