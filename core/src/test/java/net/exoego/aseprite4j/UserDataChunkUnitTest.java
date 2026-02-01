package net.exoego.aseprite4j;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

public class UserDataChunkUnitTest {

    @Test
    void readEmptyUserData() throws IOException {
        // flags = 0 (empty)
        var bytes = new byte[]{0x00, 0x00, 0x00, 0x00};
        var reader = new InputStreamReader(new ByteArrayInputStream(bytes));
        var chunk = UserDataChunk.build(100, reader);

        assertThat(chunk.getMaybeText()).isEmpty();
        assertThat(chunk.getMaybeColor()).isEmpty();
        assertThat(chunk.getMaybeProperties()).isEmpty();
    }

    @Test
    void readUserDataWithTextOnly() throws IOException {
        var baos = new ByteArrayOutputStream();
        // flags = 1 (has text)
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        // STRING "hello" = WORD(5) + bytes
        baos.write(new byte[]{0x05, 0x00});
        baos.write("hello".getBytes());

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        var chunk = UserDataChunk.build(100, reader);

        assertThat(chunk.getMaybeText()).hasValue("hello");
        assertThat(chunk.getMaybeColor()).isEmpty();
    }

    @Test
    void readUserDataWithColorOnly() throws IOException {
        var baos = new ByteArrayOutputStream();
        // flags = 2 (has color)
        baos.write(new byte[]{0x02, 0x00, 0x00, 0x00});
        // RGBA (255, 128, 64, 200)
        baos.write(new byte[]{(byte) 0xFF, (byte) 0x80, 0x40, (byte) 0xC8});

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        var chunk = UserDataChunk.build(100, reader);

        assertThat(chunk.getMaybeText()).isEmpty();
        assertThat(chunk.getMaybeColor()).isPresent();
        var color = chunk.getMaybeColor().get();
        assertThat(color.r()).isEqualTo((short) 255);
        assertThat(color.g()).isEqualTo((short) 128);
        assertThat(color.b()).isEqualTo((short) 64);
        assertThat(color.a()).isEqualTo((short) 200);
    }

    @Test
    void readUserDataWithTextAndColor() throws IOException {
        var baos = new ByteArrayOutputStream();
        // flags = 3 (has text + color)
        baos.write(new byte[]{0x03, 0x00, 0x00, 0x00});
        // STRING "test"
        baos.write(new byte[]{0x04, 0x00});
        baos.write("test".getBytes());
        // RGBA (10, 20, 30, 40)
        baos.write(new byte[]{0x0A, 0x14, 0x1E, 0x28});

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        var chunk = UserDataChunk.build(100, reader);

        assertThat(chunk.getMaybeText()).hasValue("test");
        assertThat(chunk.getMaybeColor()).isPresent();
    }

    @Test
    void readUserDataWithProperties() throws IOException {
        var baos = new ByteArrayOutputStream();
        // flags = 4 (has properties)
        baos.write(new byte[]{0x04, 0x00, 0x00, 0x00});
        // sizeInBytes (placeholder, not validated)
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        // numberOfPropertyMaps = 1
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        // extensionKey
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        // numberOfProperties = 1
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        // propertyName = "key"
        baos.write(new byte[]{0x03, 0x00});
        baos.write("key".getBytes());
        // propertyType = 0x0D (string)
        baos.write(new byte[]{0x0D, 0x00});
        // STRING "value"
        baos.write(new byte[]{0x05, 0x00});
        baos.write("value".getBytes());

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        var chunk = UserDataChunk.build(100, reader);

        assertThat(chunk.getMaybeProperties()).isPresent();
        assertThat(chunk.getMaybeProperties().get()).containsEntry("key", "value");
    }

    @Test
    void readPropertyBool() throws IOException {
        var baos = new ByteArrayOutputStream();
        baos.write(new byte[]{0x04, 0x00, 0x00, 0x00}); // flags
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00}); // size
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00}); // 1 map
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00}); // ext key
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00}); // 1 prop
        baos.write(new byte[]{0x04, 0x00}); // name "flag"
        baos.write("flag".getBytes());
        baos.write(new byte[]{0x01, 0x00}); // type = bool
        baos.write(new byte[]{0x01}); // true

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        var chunk = UserDataChunk.build(100, reader);

        assertThat(chunk.getMaybeProperties().get().get("flag")).isEqualTo(true);
    }

    @Test
    void readPropertyInt8() throws IOException {
        var baos = new ByteArrayOutputStream();
        baos.write(new byte[]{0x04, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00}); // name "x"
        baos.write("x".getBytes());
        baos.write(new byte[]{0x02, 0x00}); // type = int8
        baos.write(new byte[]{(byte) 0xFF}); // -1 as signed byte

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        var chunk = UserDataChunk.build(100, reader);

        assertThat(chunk.getMaybeProperties().get().get("x")).isEqualTo((byte) -1);
    }

    @Test
    void readPropertyUint8() throws IOException {
        var baos = new ByteArrayOutputStream();
        baos.write(new byte[]{0x04, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00});
        baos.write("y".getBytes());
        baos.write(new byte[]{0x03, 0x00}); // type = uint8
        baos.write(new byte[]{(byte) 0xFF}); // 255

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        var chunk = UserDataChunk.build(100, reader);

        assertThat(chunk.getMaybeProperties().get().get("y")).isEqualTo((short) 255);
    }

    @Test
    void readPropertyInt16() throws IOException {
        var baos = new ByteArrayOutputStream();
        baos.write(new byte[]{0x04, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00});
        baos.write("z".getBytes());
        baos.write(new byte[]{0x04, 0x00}); // type = int16
        baos.write(new byte[]{(byte) 0xFF, (byte) 0xFF}); // -1

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        var chunk = UserDataChunk.build(100, reader);

        assertThat(chunk.getMaybeProperties().get().get("z")).isEqualTo((short) -1);
    }

    @Test
    void readPropertyUint16() throws IOException {
        var baos = new ByteArrayOutputStream();
        baos.write(new byte[]{0x04, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00});
        baos.write("w".getBytes());
        baos.write(new byte[]{0x05, 0x00}); // type = uint16
        baos.write(new byte[]{(byte) 0xFF, (byte) 0xFF}); // 65535

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        var chunk = UserDataChunk.build(100, reader);

        assertThat(chunk.getMaybeProperties().get().get("w")).isEqualTo(65535);
    }

    @Test
    void readPropertyInt32() throws IOException {
        var baos = new ByteArrayOutputStream();
        baos.write(new byte[]{0x04, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00});
        baos.write("a".getBytes());
        baos.write(new byte[]{0x06, 0x00}); // type = int32
        baos.write(new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}); // -1

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        var chunk = UserDataChunk.build(100, reader);

        assertThat(chunk.getMaybeProperties().get().get("a")).isEqualTo(-1);
    }

    @Test
    void readPropertyUint32() throws IOException {
        var baos = new ByteArrayOutputStream();
        baos.write(new byte[]{0x04, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00});
        baos.write("b".getBytes());
        baos.write(new byte[]{0x07, 0x00}); // type = uint32
        baos.write(new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}); // 4294967295

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        var chunk = UserDataChunk.build(100, reader);

        assertThat(chunk.getMaybeProperties().get().get("b")).isEqualTo(4294967295L);
    }

    @Test
    void readPropertyInt64() throws IOException {
        var baos = new ByteArrayOutputStream();
        baos.write(new byte[]{0x04, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00});
        baos.write("c".getBytes());
        baos.write(new byte[]{0x08, 0x00}); // type = int64
        baos.write(new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}); // -1

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        var chunk = UserDataChunk.build(100, reader);

        assertThat(chunk.getMaybeProperties().get().get("c")).isEqualTo(-1L);
    }

    @Test
    void readPropertyUint64() throws IOException {
        var baos = new ByteArrayOutputStream();
        baos.write(new byte[]{0x04, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00});
        baos.write("d".getBytes());
        baos.write(new byte[]{0x09, 0x00}); // type = uint64
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}); // 1

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        var chunk = UserDataChunk.build(100, reader);

        assertThat(chunk.getMaybeProperties().get().get("d")).isEqualTo(BigInteger.ONE);
    }

    @Test
    void readPropertyFixed() throws IOException {
        var baos = new ByteArrayOutputStream();
        baos.write(new byte[]{0x04, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00});
        baos.write("e".getBytes());
        baos.write(new byte[]{0x0A, 0x00}); // type = fixed
        baos.write(new byte[]{0x00, (byte) 0x80, 0x00, 0x00}); // 0.5 in 16.16

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        var chunk = UserDataChunk.build(100, reader);

        assertThat((Double) chunk.getMaybeProperties().get().get("e")).isWithin(0.001).of(0.5);
    }

    @Test
    void readPropertyFloat() throws IOException {
        var baos = new ByteArrayOutputStream();
        baos.write(new byte[]{0x04, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00});
        baos.write("f".getBytes());
        baos.write(new byte[]{0x0B, 0x00}); // type = float
        // 1.0f in IEEE 754 = 0x3F800000
        baos.write(new byte[]{0x00, 0x00, (byte) 0x80, 0x3F});

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        var chunk = UserDataChunk.build(100, reader);

        assertThat((Float) chunk.getMaybeProperties().get().get("f")).isWithin(0.001f).of(1.0f);
    }

    @Test
    void readPropertyDouble() throws IOException {
        var baos = new ByteArrayOutputStream();
        baos.write(new byte[]{0x04, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00});
        baos.write("g".getBytes());
        baos.write(new byte[]{0x0C, 0x00}); // type = double
        // 1.0d in IEEE 754 = 0x3FF0000000000000
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xF0, 0x3F});

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        var chunk = UserDataChunk.build(100, reader);

        assertThat((Double) chunk.getMaybeProperties().get().get("g")).isWithin(0.001).of(1.0);
    }

    @Test
    void readPropertyPoint() throws IOException {
        var baos = new ByteArrayOutputStream();
        baos.write(new byte[]{0x04, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00});
        baos.write("p".getBytes());
        baos.write(new byte[]{0x0E, 0x00}); // type = point
        // x = 10, y = 20
        baos.write(new byte[]{0x0A, 0x00, 0x00, 0x00}); // x
        baos.write(new byte[]{0x14, 0x00, 0x00, 0x00}); // y

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        var chunk = UserDataChunk.build(100, reader);

        var point = (Point) chunk.getMaybeProperties().get().get("p");
        assertThat(point.x()).isEqualTo(10);
        assertThat(point.y()).isEqualTo(20);
    }

    @Test
    void readPropertySize() throws IOException {
        var baos = new ByteArrayOutputStream();
        baos.write(new byte[]{0x04, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00});
        baos.write("s".getBytes());
        baos.write(new byte[]{0x0F, 0x00}); // type = size
        // width = 100, height = 200
        baos.write(new byte[]{0x64, 0x00, 0x00, 0x00}); // width
        baos.write(new byte[]{(byte) 0xC8, 0x00, 0x00, 0x00}); // height

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        var chunk = UserDataChunk.build(100, reader);

        var size = (Size) chunk.getMaybeProperties().get().get("s");
        assertThat(size.width()).isEqualTo(100);
        assertThat(size.height()).isEqualTo(200);
    }

    @Test
    void readPropertyRect() throws IOException {
        var baos = new ByteArrayOutputStream();
        baos.write(new byte[]{0x04, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        baos.write(new byte[]{0x01, 0x00});
        baos.write("r".getBytes());
        baos.write(new byte[]{0x10, 0x00}); // type = rect
        // x=5, y=10, width=50, height=100
        baos.write(new byte[]{0x05, 0x00, 0x00, 0x00}); // x
        baos.write(new byte[]{0x0A, 0x00, 0x00, 0x00}); // y
        baos.write(new byte[]{0x32, 0x00, 0x00, 0x00}); // width
        baos.write(new byte[]{0x64, 0x00, 0x00, 0x00}); // height

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        var chunk = UserDataChunk.build(100, reader);

        var rect = (Rect) chunk.getMaybeProperties().get().get("r");
        assertThat(rect.origin().x()).isEqualTo(5);
        assertThat(rect.origin().y()).isEqualTo(10);
        assertThat(rect.size().width()).isEqualTo(50);
        assertThat(rect.size().height()).isEqualTo(100);
    }

    @Test
    void readPropertyVectorHomogeneous() throws IOException {
        var baos = new ByteArrayOutputStream();
        baos.write(new byte[]{0x04, 0x00, 0x00, 0x00}); // flags
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00}); // size
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00}); // 1 map
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00}); // ext key
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00}); // 1 prop
        baos.write(new byte[]{0x01, 0x00});
        baos.write("v".getBytes());
        baos.write(new byte[]{0x11, 0x00}); // type = vector
        // vector: numberOfElements=3, elementType=0x06 (int32)
        baos.write(new byte[]{0x03, 0x00, 0x00, 0x00}); // count
        baos.write(new byte[]{0x06, 0x00}); // elementType = int32
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00}); // 1
        baos.write(new byte[]{0x02, 0x00, 0x00, 0x00}); // 2
        baos.write(new byte[]{0x03, 0x00, 0x00, 0x00}); // 3

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        var chunk = UserDataChunk.build(100, reader);

        @SuppressWarnings("unchecked")
        var list = (List<Object>) chunk.getMaybeProperties().get().get("v");
        assertThat(list).containsExactly(1, 2, 3).inOrder();
    }

    @Test
    void readPropertyVectorHeterogeneous() throws IOException {
        var baos = new ByteArrayOutputStream();
        baos.write(new byte[]{0x04, 0x00, 0x00, 0x00}); // flags
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00}); // size
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00}); // 1 map
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00}); // ext key
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00}); // 1 prop
        baos.write(new byte[]{0x01, 0x00});
        baos.write("v".getBytes());
        baos.write(new byte[]{0x11, 0x00}); // type = vector
        // vector: numberOfElements=2, elementType=0 (heterogeneous)
        baos.write(new byte[]{0x02, 0x00, 0x00, 0x00}); // count
        baos.write(new byte[]{0x00, 0x00}); // elementType = 0 (mixed)
        // element 1: type=0x06 (int32), value=42
        baos.write(new byte[]{0x06, 0x00});
        baos.write(new byte[]{0x2A, 0x00, 0x00, 0x00});
        // element 2: type=0x0D (string), value="hi"
        baos.write(new byte[]{0x0D, 0x00});
        baos.write(new byte[]{0x02, 0x00});
        baos.write("hi".getBytes());

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        var chunk = UserDataChunk.build(100, reader);

        @SuppressWarnings("unchecked")
        var list = (List<Object>) chunk.getMaybeProperties().get().get("v");
        assertThat(list).containsExactly(42, "hi").inOrder();
    }

    @Test
    void readPropertyNestedMap() throws IOException {
        var baos = new ByteArrayOutputStream();
        baos.write(new byte[]{0x04, 0x00, 0x00, 0x00}); // flags
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00}); // size
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00}); // 1 map
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00}); // ext key
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00}); // 1 prop
        baos.write(new byte[]{0x06, 0x00});
        baos.write("nested".getBytes());
        baos.write(new byte[]{0x12, 0x00}); // type = nested properties
        // nested: numberOfProperties=1
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00});
        // nested property: name="inner", type=0x06 (int32), value=99
        baos.write(new byte[]{0x05, 0x00});
        baos.write("inner".getBytes());
        baos.write(new byte[]{0x06, 0x00});
        baos.write(new byte[]{0x63, 0x00, 0x00, 0x00});

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        var chunk = UserDataChunk.build(100, reader);

        @SuppressWarnings("unchecked")
        var nested = (Map<String, Object>) chunk.getMaybeProperties().get().get("nested");
        assertThat(nested).containsEntry("inner", 99);
    }

    @Test
    void readPropertyUuid() throws IOException {
        var baos = new ByteArrayOutputStream();
        baos.write(new byte[]{0x04, 0x00, 0x00, 0x00}); // flags
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00}); // size
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00}); // 1 map
        baos.write(new byte[]{0x00, 0x00, 0x00, 0x00}); // ext key
        baos.write(new byte[]{0x01, 0x00, 0x00, 0x00}); // 1 prop
        baos.write(new byte[]{0x02, 0x00});
        baos.write("id".getBytes());
        baos.write(new byte[]{0x13, 0x00}); // type = uuid
        // 16 bytes UUID
        baos.write(new byte[]{
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x10
        });

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        var chunk = UserDataChunk.build(100, reader);

        var uuid = chunk.getMaybeProperties().get().get("id");
        assertThat(uuid).isInstanceOf(java.util.UUID.class);
    }

    @Test
    void equalsAndHashCode() {
        var chunk1 = new UserDataChunk("text", new Color.RGBA((short) 1, (short) 2, (short) 3, (short) 4), null);
        var chunk2 = new UserDataChunk("text", new Color.RGBA((short) 1, (short) 2, (short) 3, (short) 4), null);
        var chunk3 = new UserDataChunk("different", null, null);

        assertThat(chunk1).isEqualTo(chunk2);
        assertThat(chunk1.hashCode()).isEqualTo(chunk2.hashCode());
        assertThat(chunk1).isNotEqualTo(chunk3);
    }

    @Test
    void toStringContainsFields() {
        var chunk = new UserDataChunk("hello", null, null);
        assertThat(chunk.toString()).contains("hello");
    }
}
