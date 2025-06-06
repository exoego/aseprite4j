package net.exoego.aseprite4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

final class InputStreamReader {
    private final InputStream in;

    InputStreamReader(InputStream in) {
        this.in = in;
    }

    static short toUnsignedByte(byte[] bytes) {
        assert bytes.length == 1;
        return (short) (0xFF & bytes[0]);
    }

    static short toShort(byte[] bytes) {
        assert bytes.length == 2;
        return (short) (((bytes[1] & 0xFF) << 8) | (bytes[0] & 0xFF));
    }

    static int toUnsignedShort(byte[] bytes) {
        assert bytes.length == 2;
        return ((bytes[1] << 8) & 0xFF00) | (0xFF & bytes[0]);
    }

    static int toInt(byte[] bytes) {
        return (int) toUnsignedInt(bytes);
    }

    static long toUnsignedInt(byte[] bytes) {
        assert bytes.length == 4;
        return (bytes[3] << 24) |
                ((bytes[2] << 16) & 0xFF0000) |
                ((bytes[1] << 8) & 0xFF00) |
                (bytes[0] & 0xFF);
    }

     byte[] readNBytes(int n) throws IOException {
        byte[] bytes = new byte[n];
        in.read(bytes);
        return bytes;
    }

    void skip(int n) throws IOException {
        in.skipNBytes(n);
    }

    void skip(long n) throws IOException {
        in.skipNBytes(n);
    }

    /**
     * An 8-bit unsigned integer value
     */
    short BYTE() throws IOException {
        return toUnsignedByte(readNBytes(1));
    }

    /**
     * A 16-bit unsigned integer value
     */
    int WORD() throws IOException {
        return toUnsignedShort(readNBytes(2));
    }

    /**
     * A 16-bit signed integer value
     */
    int SHORT() throws IOException {
        return toShort(readNBytes(2));
    }

    /**
     * A 32-bit unsigned integer value
     */
    long DWORD() throws IOException {
        return toUnsignedInt(readNBytes(4));
    }

    String STRING() throws IOException {
        int len = WORD();
        var buf = readNBytes(len);
        return new String(buf);
    }

    UUID UUID() throws IOException {
        var uuidBytes = readNBytes(16);
        return UUID.nameUUIDFromBytes(uuidBytes);
    }

    Pixel PIXEL(ColorDepth depth) throws IOException {
        return switch (depth) {
            case RGBA -> {
                short r = BYTE();
                short g = BYTE();
                short b = BYTE();
                short a = BYTE();
                yield new Pixel.RGBA(r, g, b, a);
            }
            case Grayscale -> {
                short value = BYTE();
                short alpha = BYTE();
                yield new Pixel.Grayscale(value, alpha);
            }
            case Indexed -> {
                short index = BYTE();
                yield new Pixel.Index(index);
            }
        };
    }

    Tile TILE(int bitsPerTile) throws  IOException {
        return switch (bitsPerTile) {
            case 8 -> new Tile.Tile8(BYTE());
            case 16 -> new Tile.Tile16(WORD());
            case 32 -> new Tile.Tile32(DWORD());
            default -> throw new IllegalArgumentException("Unsupported bits per tile: " + bitsPerTile);
        };
    }

    double FIXED() throws IOException {
        double num0 = WORD();
        double num1 = WORD();
        while (num1 > 0) {
            num1 /= 10.0;
        }
        return num0 + num1;
    }
}
