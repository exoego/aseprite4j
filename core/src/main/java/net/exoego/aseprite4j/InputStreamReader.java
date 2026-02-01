package net.exoego.aseprite4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;
import java.util.zip.InflaterInputStream;

public final class InputStreamReader {
    private final InputStream in;
    private final boolean debugEnabled;

    InputStreamReader(InputStream in) {
        this(in, false);
    }

    InputStreamReader(InputStream in, boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
        this.in = debugEnabled ? new DebugInputStream(in) : in;
    }

    public String currentAddress() {
        if (debugEnabled && in instanceof DebugInputStream debugIn) {
            return debugIn.currentAddress();
        }
        return "debug mode disabled";
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
        assert bytes.length == 4;
        return ((bytes[3] & 0xFF) << 24) |
                ((bytes[2] & 0xFF) << 16) |
                ((bytes[1] & 0xFF) << 8) |
                (bytes[0] & 0xFF);
    }

    static long toUnsignedInt(byte[] bytes) {
        assert bytes.length == 4;
        return ((long) (bytes[3] & 0xFF) << 24) |
                ((bytes[2] & 0xFF) << 16) |
                ((bytes[1] & 0xFF) << 8) |
                (bytes[0] & 0xFF);
    }

    static long toLong(byte[] bytes) {
        assert bytes.length == 8;
        return ((long) (bytes[7] & 0xFF) << 56) |
                ((long) (bytes[6] & 0xFF) << 48) |
                ((long) (bytes[5] & 0xFF) << 40) |
                ((long) (bytes[4] & 0xFF) << 32) |
                ((long) (bytes[3] & 0xFF) << 24) |
                ((long) (bytes[2] & 0xFF) << 16) |
                ((long) (bytes[1] & 0xFF) << 8) |
                (bytes[0] & 0xFF);
    }

    private int bytesRead = 0;

    byte[] readNBytes(int n) throws IOException {
        var bytes = in.readNBytes(n);
        bytesRead += bytes.length;
        return bytes;
    }

    void skip(int n) throws IOException {
        bytesRead += n;
        in.skipNBytes(n);
    }

    void skip(long n) throws IOException {
        bytesRead += (int) n;
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
     * A 32-bit signed integer value
     */
    int INT32() throws IOException {
        return toInt(readNBytes(4));
    }

    /**
     * A 32-bit signed integer value (alias for INT32)
     */
    int LONG() throws IOException {
        return INT32();
    }

    /**
     * A 64-bit signed integer value
     */
    long INT64() throws IOException {
        return toLong(readNBytes(8));
    }

    /**
     * A 64-bit signed integer value (alias for INT64)
     */
    long LONG64() throws IOException {
        return INT64();
    }

    /**
     * A 64-bit unsigned integer value
     */
    BigInteger QWORD() throws IOException {
        byte[] bytes = readNBytes(8);
        // Convert little-endian to big-endian for BigInteger
        byte[] bigEndian = new byte[9]; // Extra byte for sign (unsigned)
        bigEndian[0] = 0; // positive sign
        for (int i = 0; i < 8; i++) {
            bigEndian[i + 1] = bytes[7 - i];
        }
        return new BigInteger(bigEndian);
    }

    /**
     * A 32-bit unsigned integer value
     */
    long DWORD() throws IOException {
        return toUnsignedInt(readNBytes(4));
    }

    String STRING() throws IOException {
        int len = WORD();
        if (len == 0) {
            return "";
        }
        var buf = readNBytes(len);
        return new String(buf);
    }

    UUID UUID() throws IOException {
        byte[] uuidBytes = readNBytes(16);
        // Construct UUID from raw bytes (little-endian format)
        // UUID format: 4 bytes (time_low) + 2 bytes (time_mid) + 2 bytes (time_hi_and_version) +
        //              1 byte (clock_seq_hi) + 1 byte (clock_seq_low) + 6 bytes (node)
        long mostSigBits = 0;
        long leastSigBits = 0;

        // First 8 bytes -> mostSigBits (but in little-endian order per component)
        // time_low (4 bytes, little-endian)
        mostSigBits |= ((long) (uuidBytes[3] & 0xFF)) << 56;
        mostSigBits |= ((long) (uuidBytes[2] & 0xFF)) << 48;
        mostSigBits |= ((long) (uuidBytes[1] & 0xFF)) << 40;
        mostSigBits |= ((long) (uuidBytes[0] & 0xFF)) << 32;
        // time_mid (2 bytes, little-endian)
        mostSigBits |= ((long) (uuidBytes[5] & 0xFF)) << 24;
        mostSigBits |= ((long) (uuidBytes[4] & 0xFF)) << 16;
        // time_hi_and_version (2 bytes, little-endian)
        mostSigBits |= ((long) (uuidBytes[7] & 0xFF)) << 8;
        mostSigBits |= ((long) (uuidBytes[6] & 0xFF));

        // Last 8 bytes -> leastSigBits (clock_seq and node, big-endian)
        for (int i = 8; i < 16; i++) {
            leastSigBits = (leastSigBits << 8) | (uuidBytes[i] & 0xFF);
        }

        return new UUID(mostSigBits, leastSigBits);
    }

    Pixel PIXEL(ColorDepth depth) throws IOException {
        var bytesPerPixel = depth.getBitsPerPixel() / 8;
        var buffer = readNBytes(bytesPerPixel);
        return switch (depth) {
            case RGBA -> new Pixel.RGBA(
                    (short) (buffer[0] & 0xFF),
                    (short) (buffer[1] & 0xFF),
                    (short) (buffer[2] & 0xFF),
                    (short) (buffer[3] & 0xFF));
            case Grayscale -> new Pixel.Grayscale(
                    (short) (buffer[0] & 0xFF),
                    (short) (buffer[1] & 0xFF));
            case Indexed -> new Pixel.Index((short) (buffer[0] & 0xFF));
        };
    }

    Tile TILE(int bitsPerTile) throws IOException {
        return switch (bitsPerTile) {
            case 8 -> new Tile.Tile8(BYTE());
            case 16 -> new Tile.Tile16(WORD());
            case 32 -> new Tile.Tile32(DWORD());
            default -> throw new IllegalArgumentException("Unsupported bits per tile: " + bitsPerTile);
        };
    }

    /**
     * A 32-bit fixed point (16.16) value.
     * The integer part is in the high 16 bits, the fractional part in the low 16 bits.
     */
    double FIXED() throws IOException {
        int raw = toInt(readNBytes(4));
        return raw / 65536.0;
    }

    /**
     * A 32-bit IEEE 754 single-precision floating point value
     */
    float FLOAT() throws IOException {
        byte[] bytes = readNBytes(4);
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        return buffer.getFloat();
    }

    /**
     * A 64-bit IEEE 754 double-precision floating point value
     */
    double DOUBLE() throws IOException {
        byte[] bytes = readNBytes(8);
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        return buffer.getDouble();
    }

    /**
     * A point (two 32-bit signed integers: x, y)
     */
    Point POINT() throws IOException {
        int x = INT32();
        int y = INT32();
        return new Point(x, y);
    }

    /**
     * A size (two 32-bit signed integers: width, height)
     */
    Size SIZE() throws IOException {
        int width = INT32();
        int height = INT32();
        return new Size(width, height);
    }

    /**
     * A rectangle (origin point + size)
     */
    Rect RECT() throws IOException {
        Point origin = POINT();
        Size size = SIZE();
        return new Rect(origin, size);
    }

    InputStreamReader decompressZlib(int chunkSize) throws IOException {
        var bytes = this.readNBytes(chunkSize);
        var bais = new ByteArrayInputStream(bytes);
        var iis = new InflaterInputStream(bais);
        return new InputStreamReader(iis);
    }

    Pixel[] PIXELS(int count, ColorDepth colorDepth) throws IOException {
        int bytesPerPixel = colorDepth.getBitsPerPixel() / 8;
        var buffer = readNBytes(count * bytesPerPixel);
        var pixels = new Pixel[count];

        int offset = 0;
        switch (colorDepth) {
            case RGBA -> {
                for (int i = 0; i < count; i++) {
                    pixels[i] = new Pixel.RGBA(
                            (short) (buffer[offset++] & 0xFF),
                            (short) (buffer[offset++] & 0xFF),
                            (short) (buffer[offset++] & 0xFF),
                            (short) (buffer[offset++] & 0xFF));
                }
            }
            case Grayscale -> {
                for (int i = 0; i < count; i++) {
                    pixels[i] = new Pixel.Grayscale(
                            (short) (buffer[offset++] & 0xFF),
                            (short) (buffer[offset++] & 0xFF));
                }
            }
            case Indexed -> {
                for (int i = 0; i < count; i++) {
                    pixels[i] = new Pixel.Index((short) (buffer[offset++] & 0xFF));
                }
            }
        }
        return pixels;
    }

    Tile[] TILES(int size, int bitsPerTile) throws IOException {
        var tiles = new Tile[size];
        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = this.TILE(bitsPerTile);
        }
        return tiles;
    }

    interface Block<R> {
        R run() throws IOException;
    }

    <R> R checkSize(int expectedSize, Block<R> block) throws IOException {
        bytesRead = 0;
        var r = block.run();
        if (bytesRead != expectedSize) {
            throw new IOException("Expected size: " + expectedSize + ", but read: " + bytesRead + " bytes at " + currentAddress());
        }
        return r;
    }
}
