package net.exoego.aseprite4j;

public enum CelType {
    /**
     * Unused, compressed image is preferred
     */
    RAW_IMAGE_DATA(0),
    LINKED_CEL(1),
    COMPRESSED_IMAGE(2),
    COMPRESSED_TILEMAP(3);

    private final int value;

    CelType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static CelType from(int value) {
        return switch (value) {
            case 0 -> RAW_IMAGE_DATA;
            case 1 -> LINKED_CEL;
            case 2 -> COMPRESSED_IMAGE;
            case 3 -> COMPRESSED_TILEMAP;
            default -> throw new IllegalArgumentException("Invalid cel type value: " + value);
        };
    }
}
