package net.exoego.aseprite4j;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;

public class InputStreamReaderTest {

    @Test
    void testBYTE() throws IOException {
        var bytes = new byte[]{(byte) 0x00, (byte) 0x7F, (byte) 0x80, (byte) 0xFF};
        var reader = new InputStreamReader(new ByteArrayInputStream(bytes));
        assertThat(reader.BYTE()).isEqualTo((short) 0);
        assertThat(reader.BYTE()).isEqualTo((short) 127);
        assertThat(reader.BYTE()).isEqualTo((short) 128);
        assertThat(reader.BYTE()).isEqualTo((short) 255);
    }

    @Test
    void testWORD() throws IOException {
        // Little-endian 16-bit unsigned
        var bytes = new byte[]{
                0x00, 0x00,   // 0
                0x01, 0x00,   // 1
                (byte) 0xFF, 0x00, // 255
                0x00, 0x01,   // 256
                (byte) 0xFF, (byte) 0xFF  // 65535
        };
        var reader = new InputStreamReader(new ByteArrayInputStream(bytes));
        assertThat(reader.WORD()).isEqualTo(0);
        assertThat(reader.WORD()).isEqualTo(1);
        assertThat(reader.WORD()).isEqualTo(255);
        assertThat(reader.WORD()).isEqualTo(256);
        assertThat(reader.WORD()).isEqualTo(65535);
    }

    @Test
    void testSHORT() throws IOException {
        // Little-endian 16-bit signed
        var bytes = new byte[]{
                0x00, 0x00,   // 0
                0x01, 0x00,   // 1
                (byte) 0xFF, (byte) 0xFF, // -1
                0x00, (byte) 0x80,   // -32768
                (byte) 0xFF, 0x7F    // 32767
        };
        var reader = new InputStreamReader(new ByteArrayInputStream(bytes));
        assertThat(reader.SHORT()).isEqualTo(0);
        assertThat(reader.SHORT()).isEqualTo(1);
        assertThat(reader.SHORT()).isEqualTo(-1);
        assertThat(reader.SHORT()).isEqualTo(-32768);
        assertThat(reader.SHORT()).isEqualTo(32767);
    }

    @Test
    void testINT32() throws IOException {
        // Little-endian 32-bit signed
        var bytes = new byte[]{
                0x00, 0x00, 0x00, 0x00,   // 0
                0x01, 0x00, 0x00, 0x00,   // 1
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, // -1
                0x00, 0x00, 0x00, (byte) 0x80,   // Integer.MIN_VALUE
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x7F    // Integer.MAX_VALUE
        };
        var reader = new InputStreamReader(new ByteArrayInputStream(bytes));
        assertThat(reader.INT32()).isEqualTo(0);
        assertThat(reader.INT32()).isEqualTo(1);
        assertThat(reader.INT32()).isEqualTo(-1);
        assertThat(reader.INT32()).isEqualTo(Integer.MIN_VALUE);
        assertThat(reader.INT32()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    void testDWORD() throws IOException {
        // Little-endian 32-bit unsigned
        var bytes = new byte[]{
                0x00, 0x00, 0x00, 0x00,   // 0
                0x01, 0x00, 0x00, 0x00,   // 1
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, // 4294967295
        };
        var reader = new InputStreamReader(new ByteArrayInputStream(bytes));
        assertThat(reader.DWORD()).isEqualTo(0L);
        assertThat(reader.DWORD()).isEqualTo(1L);
        assertThat(reader.DWORD()).isEqualTo(4294967295L);
    }

    @Test
    void testINT64() throws IOException {
        // Little-endian 64-bit signed
        var bytes = new byte[]{
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,   // 0
                0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,   // 1
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, // -1
        };
        var reader = new InputStreamReader(new ByteArrayInputStream(bytes));
        assertThat(reader.INT64()).isEqualTo(0L);
        assertThat(reader.INT64()).isEqualTo(1L);
        assertThat(reader.INT64()).isEqualTo(-1L);
    }

    @Test
    void testQWORD() throws IOException {
        // Little-endian 64-bit unsigned
        var bytes = new byte[]{
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,   // 0
                0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,   // 1
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, // max unsigned 64-bit
        };
        var reader = new InputStreamReader(new ByteArrayInputStream(bytes));
        assertThat(reader.QWORD()).isEqualTo(BigInteger.ZERO);
        assertThat(reader.QWORD()).isEqualTo(BigInteger.ONE);
        assertThat(reader.QWORD()).isEqualTo(new BigInteger("18446744073709551615"));
    }

    @Test
    void testFIXED() throws IOException {
        // 16.16 fixed point: value = raw / 65536.0
        var bytes = new byte[]{
                // 1.0 = 0x00010000 = 65536
                0x00, 0x00, 0x01, 0x00,
                // 0.5 = 0x00008000 = 32768
                0x00, (byte) 0x80, 0x00, 0x00,
                // 2.5 = 0x00028000 = 163840
                0x00, (byte) 0x80, 0x02, 0x00,
                // -1.0 = 0xFFFF0000 (two's complement)
                0x00, 0x00, (byte) 0xFF, (byte) 0xFF,
        };
        var reader = new InputStreamReader(new ByteArrayInputStream(bytes));
        assertThat(reader.FIXED()).isWithin(0.0001).of(1.0);
        assertThat(reader.FIXED()).isWithin(0.0001).of(0.5);
        assertThat(reader.FIXED()).isWithin(0.0001).of(2.5);
        assertThat(reader.FIXED()).isWithin(0.0001).of(-1.0);
    }

    @Test
    void testFLOAT() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(12).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putFloat(1.0f);
        buffer.putFloat(0.5f);
        buffer.putFloat(-2.5f);

        var reader = new InputStreamReader(new ByteArrayInputStream(buffer.array()));
        assertThat(reader.FLOAT()).isWithin(0.0001f).of(1.0f);
        assertThat(reader.FLOAT()).isWithin(0.0001f).of(0.5f);
        assertThat(reader.FLOAT()).isWithin(0.0001f).of(-2.5f);
    }

    @Test
    void testDOUBLE() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(24).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putDouble(1.0);
        buffer.putDouble(0.5);
        buffer.putDouble(-2.5);

        var reader = new InputStreamReader(new ByteArrayInputStream(buffer.array()));
        assertThat(reader.DOUBLE()).isWithin(0.0001).of(1.0);
        assertThat(reader.DOUBLE()).isWithin(0.0001).of(0.5);
        assertThat(reader.DOUBLE()).isWithin(0.0001).of(-2.5);
    }

    @Test
    void testPOINT() throws IOException {
        // Two INT32 values (x, y)
        var bytes = new byte[]{
                0x0A, 0x00, 0x00, 0x00,   // x = 10
                0x14, 0x00, 0x00, 0x00,   // y = 20
        };
        var reader = new InputStreamReader(new ByteArrayInputStream(bytes));
        var point = reader.POINT();
        assertThat(point.x()).isEqualTo(10);
        assertThat(point.y()).isEqualTo(20);
    }

    @Test
    void testSIZE() throws IOException {
        // Two INT32 values (width, height)
        var bytes = new byte[]{
                0x64, 0x00, 0x00, 0x00,   // width = 100
                (byte) 0xC8, 0x00, 0x00, 0x00,   // height = 200
        };
        var reader = new InputStreamReader(new ByteArrayInputStream(bytes));
        var size = reader.SIZE();
        assertThat(size.width()).isEqualTo(100);
        assertThat(size.height()).isEqualTo(200);
    }

    @Test
    void testRECT() throws IOException {
        // POINT (origin) + SIZE
        var bytes = new byte[]{
                0x0A, 0x00, 0x00, 0x00,   // origin.x = 10
                0x14, 0x00, 0x00, 0x00,   // origin.y = 20
                0x64, 0x00, 0x00, 0x00,   // size.width = 100
                (byte) 0xC8, 0x00, 0x00, 0x00,   // size.height = 200
        };
        var reader = new InputStreamReader(new ByteArrayInputStream(bytes));
        var rect = reader.RECT();
        assertThat(rect.origin().x()).isEqualTo(10);
        assertThat(rect.origin().y()).isEqualTo(20);
        assertThat(rect.size().width()).isEqualTo(100);
        assertThat(rect.size().height()).isEqualTo(200);
    }

    @Test
    void testSTRING() throws IOException {
        // WORD (length) + bytes
        var bytes = new byte[]{
                0x05, 0x00,   // length = 5
                'H', 'e', 'l', 'l', 'o',
                0x00, 0x00,   // length = 0 (empty string)
        };
        var reader = new InputStreamReader(new ByteArrayInputStream(bytes));
        assertThat(reader.STRING()).isEqualTo("Hello");
        assertThat(reader.STRING()).isEqualTo("");
    }

    @Test
    void testUUID() throws IOException {
        // UUID bytes in little-endian format
        // Example: 12345678-1234-1234-1234-123456789ABC
        var bytes = new byte[]{
                // time_low (4 bytes, little-endian)
                0x78, 0x56, 0x34, 0x12,
                // time_mid (2 bytes, little-endian)
                0x34, 0x12,
                // time_hi_and_version (2 bytes, little-endian)
                0x34, 0x12,
                // clock_seq and node (8 bytes, big-endian)
                0x12, 0x34, 0x12, 0x34, 0x56, 0x78, (byte) 0x9A, (byte) 0xBC
        };
        var reader = new InputStreamReader(new ByteArrayInputStream(bytes));
        var uuid = reader.UUID();
        assertThat(uuid).isEqualTo(UUID.fromString("12345678-1234-1234-1234-123456789abc"));
    }

    @Test
    void testLONG_isAliasForINT32() throws IOException {
        var bytes = new byte[]{
                0x01, 0x00, 0x00, 0x00,   // 1
        };
        var reader = new InputStreamReader(new ByteArrayInputStream(bytes));
        assertThat(reader.LONG()).isEqualTo(1);
    }

    @Test
    void testLONG64_isAliasForINT64() throws IOException {
        var bytes = new byte[]{
                0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,   // 1
        };
        var reader = new InputStreamReader(new ByteArrayInputStream(bytes));
        assertThat(reader.LONG64()).isEqualTo(1L);
    }
}
