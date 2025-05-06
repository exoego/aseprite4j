package net.exoego.aseprite4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;

final class AsepriteBuffer {
    private final ByteBuffer byteBuffer;

    private AsepriteBuffer(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    static AsepriteBuffer from(InputStream in) throws IOException {
        var byteBuffer = ByteBuffer.allocate(in.available()).order(ByteOrder.LITTLE_ENDIAN);
        Channels.newChannel(in).read(byteBuffer);
        return new AsepriteBuffer(byteBuffer);
    }

    /**
     * An 8-bit unsigned integer value
     */
    private int BYTE(int index) {
        return Byte.toUnsignedInt(byteBuffer.get(index));
    }

    /**
     * A 16-bit unsigned integer value
     */
     int WORD(int index) {
        return Short.toUnsignedInt(byteBuffer.getShort(index));
    }

    /**
     * A 16-bit signed integer value
     */
     int SHORT(int index) {
        return Short.toUnsignedInt(byteBuffer.getShort(index));
    }

    /**
     * A 32-bit unsigned integer value
     */
    long DWORD(int index) {
        return Integer.toUnsignedLong(byteBuffer.getInt(index));
    }

    /**
     * A 32-bit signed integer value
     */
    int LONG(int index) {
        return byteBuffer.getInt(index);
    }

    /**
     * STRING:
     * - WORD: string length (number of bytes)
     * - BYTE[length]: characters (in UTF-8) The '\0' character is not included.
     */
    String STRING(int index) {
        int length = stringLength(index);
        byte[] dst = new byte[length];
        byteBuffer.position(index + 2);
        byteBuffer.get(dst, 0, length);
        return new String(dst, StandardCharsets.UTF_8);
    }

    private int stringLength(int index) {
        return WORD(index);
    }

}
