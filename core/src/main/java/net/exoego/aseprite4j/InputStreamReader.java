package net.exoego.aseprite4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

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

    private byte[] readNBytes(int n) {
        byte[] bytes = new byte[n];
        try {
            in.read(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return bytes;
    }

    void skip(int n) {
        readNBytes(n);
    }

    /**
     * An 8-bit unsigned integer value
     */
    short BYTE() {
        return toUnsignedByte(readNBytes(1));
    }

    /**
     * A 16-bit unsigned integer value
     */
    int WORD() {
        return toUnsignedShort(readNBytes(2));
    }

    /**
     * A 16-bit signed integer value
     */
    int SHORT() {
        return toShort(readNBytes(2));
    }

    /**
     * A 32-bit unsigned integer value
     */
    long DWORD() {
        return toUnsignedInt(readNBytes(4));
    }

    /**
     * A 32-bit signed integer value
     */
    int LONG(int index) {
        return toInt(readNBytes(4));
    }
//
//    /**
//     * STRING:
//     * - WORD: string length (number of bytes)
//     * - BYTE[length]: characters (in UTF-8) The '\0' character is not included.
//     */
//    String STRING(int index) {
//        int length = stringLength(index);
//        byte[] dst = new byte[length];
//        byteBuffer.position(index + 2);
//        byteBuffer.get(dst, 0, length);
//        return new String(dst, StandardCharsets.UTF_8);
//    }
//
//    private int stringLength(int index) {
//        return WORD(index);
//    }

}
