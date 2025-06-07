package net.exoego.aseprite4j;

public interface Tile {
    record Tile8(short value) implements Tile {
    }

    record Tile16(int value) implements Tile {
    }

    record Tile32(long value) implements Tile {
    }
}
