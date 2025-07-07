package net.exoego.aseprite4j;

public enum LoopAnimationDirection {
    FORWARD((byte) 0),
    REVERSE((byte) 1),
    PINGPONG((byte) 2),
    PINGPONG_REVERSE((byte) 3);

    private final byte value;

    LoopAnimationDirection(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static LoopAnimationDirection from(byte value) {
        return switch (value) {
            case 0 -> FORWARD;
            case 1 -> REVERSE;
            case 2 -> PINGPONG;
            case 3 -> PINGPONG_REVERSE;
            default -> throw new IllegalArgumentException("Unknown LoopAnimationDirection value: " + value);
        };
    }
}
