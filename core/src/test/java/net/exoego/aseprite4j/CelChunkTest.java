package net.exoego.aseprite4j;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

import static com.google.common.truth.Truth.assertThat;

public class CelChunkTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "1empty3",
            "2f-index-3x3",
            "2x2-index-2frame",
            "3x2tilemap-grayscale",
            "4f-index-4x4",
            "abcd",
            "bg-index-3",
            "cut_paste",
            "file-tests-props",
            "groups2",
            "groups3abc",
            "link",
            "my-double-height",
            "my-double-wide",
            "my-w100-h200",
            "my-w128-h64-grayscale",
            "point2frames",
            "point4frames",
            "slices",
            "slices-moving",
            "tags3",
            "tags3x123reps"
    })
    void readCompressedImageCelChunk(String filename) throws Exception {
        var path = Paths.get(getClass().getResource("/aseprite/sprites/" + filename + ".aseprite").toURI());
        var file = AsepriteFile.read(path);

        var chunks = file.frames().stream()
                .flatMap(f -> f.chunks().stream())
                .filter(c -> c instanceof CelChunk.CompressedImageCelChunk)
                .map(c -> (CelChunk.CompressedImageCelChunk) c)
                .toList();

        assertThat(chunks).isNotEmpty();
        for (var chunk : chunks) {
            assertThat(chunk.widthInPixels()).isGreaterThan(0);
            assertThat(chunk.heightInPixels()).isGreaterThan(0);
            assertThat(chunk.pixelData()).isNotNull();
            assertThat(chunk.pixelData().length).isEqualTo(chunk.widthInPixels() * chunk.heightInPixels());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2x2tilemap2x2tile",
            "2x3tilemap-indexed",
            "file-tests-props"
    })
    void readCompressedTilemapCelChunk(String filename) throws Exception {
        var path = Paths.get(getClass().getResource("/aseprite/sprites/" + filename + ".aseprite").toURI());
        var file = AsepriteFile.read(path);

        var chunks = file.frames().stream()
                .flatMap(f -> f.chunks().stream())
                .filter(c -> c instanceof CelChunk.CompressedTilemapCelChunk)
                .map(c -> (CelChunk.CompressedTilemapCelChunk) c)
                .toList();

        assertThat(chunks).isNotEmpty();
        for (var chunk : chunks) {
            assertThat(chunk.widthInNumberOfTiles()).isGreaterThan(0);
            assertThat(chunk.heightInNumberOfTiles()).isGreaterThan(0);
            assertThat(chunk.tileData()).isNotNull();
            assertThat(chunk.tileData().length).isEqualTo(chunk.widthInNumberOfTiles() * chunk.heightInNumberOfTiles());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "link",
            "slices-moving",
            "tags3",
            "tags3x123reps"
    })
    void readLinkedCelChunk(String filename) throws Exception {
        var path = Paths.get(getClass().getResource("/aseprite/sprites/" + filename + ".aseprite").toURI());
        var file = AsepriteFile.read(path);

        var chunks = file.frames().stream()
                .flatMap(f -> f.chunks().stream())
                .filter(c -> c instanceof CelChunk.LinkedCelCelChunk)
                .map(c -> (CelChunk.LinkedCelCelChunk) c)
                .toList();

        assertThat(chunks).isNotEmpty();
        for (var chunk : chunks) {
            assertThat(chunk.framePositionToLinkWith()).isAtLeast(0);
        }
    }

    @Test
    void readRawImageDataCelChunk() throws IOException {
        var baos = new ByteArrayOutputStream();
        // WORD layerIndex = 0
        baos.write(new byte[]{0x00, 0x00});
        // SHORT xPosition = 10
        baos.write(new byte[]{0x0A, 0x00});
        // SHORT yPosition = 20
        baos.write(new byte[]{0x14, 0x00});
        // BYTE opacityLevel = 255
        baos.write(new byte[]{(byte) 0xFF});
        // WORD celType = 0 (RAW_IMAGE_DATA)
        baos.write(new byte[]{0x00, 0x00});
        // SHORT zIndex = 0
        baos.write(new byte[]{0x00, 0x00});
        // 5 bytes reserved (future)
        baos.write(new byte[5]);
        // WORD widthInPixels = 2
        baos.write(new byte[]{0x02, 0x00});
        // WORD heightInPixels = 2
        baos.write(new byte[]{0x02, 0x00});
        // 4 pixels in Indexed color depth (1 byte each)
        baos.write(new byte[]{0x01, 0x02, 0x03, 0x04});

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        // chunkSize = header (16) + width(2) + height(2) + pixels(4) = 24
        var chunk = CelChunk.build(reader, 24, ColorDepth.Indexed);

        assertThat(chunk).isInstanceOf(CelChunk.RawImageDataCelChunk.class);
        var rawChunk = (CelChunk.RawImageDataCelChunk) chunk;
        assertThat(rawChunk.layerIndex()).isEqualTo(0);
        assertThat(rawChunk.xPosition()).isEqualTo(10);
        assertThat(rawChunk.yPosition()).isEqualTo(20);
        assertThat(rawChunk.opacityLevel()).isEqualTo((short) 255);
        assertThat(rawChunk.celType()).isEqualTo(CelType.RAW_IMAGE_DATA);
        assertThat(rawChunk.widthInPixels()).isEqualTo(2);
        assertThat(rawChunk.heightInPixels()).isEqualTo(2);
        assertThat(rawChunk.pixelData()).hasLength(4);
    }

    @Test
    void readRawImageDataCelChunkRGBA() throws IOException {
        var baos = new ByteArrayOutputStream();
        // WORD layerIndex = 1
        baos.write(new byte[]{0x01, 0x00});
        // SHORT xPosition = 0
        baos.write(new byte[]{0x00, 0x00});
        // SHORT yPosition = 0
        baos.write(new byte[]{0x00, 0x00});
        // BYTE opacityLevel = 128
        baos.write(new byte[]{(byte) 0x80});
        // WORD celType = 0 (RAW_IMAGE_DATA)
        baos.write(new byte[]{0x00, 0x00});
        // SHORT zIndex = 1
        baos.write(new byte[]{0x01, 0x00});
        // 5 bytes reserved (future)
        baos.write(new byte[5]);
        // WORD widthInPixels = 1
        baos.write(new byte[]{0x01, 0x00});
        // WORD heightInPixels = 1
        baos.write(new byte[]{0x01, 0x00});
        // 1 pixel in RGBA color depth (4 bytes: R, G, B, A)
        baos.write(new byte[]{(byte) 0xFF, (byte) 0x80, 0x40, (byte) 0xFF});

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        // chunkSize = header (16) + width(2) + height(2) + pixels(4) = 24
        var chunk = CelChunk.build(reader, 24, ColorDepth.RGBA);

        assertThat(chunk).isInstanceOf(CelChunk.RawImageDataCelChunk.class);
        var rawChunk = (CelChunk.RawImageDataCelChunk) chunk;
        assertThat(rawChunk.pixelData()).hasLength(1);
        var pixel = (Pixel.RGBA) rawChunk.pixelData()[0];
        assertThat(pixel.r()).isEqualTo((short) 255);
        assertThat(pixel.g()).isEqualTo((short) 128);
        assertThat(pixel.b()).isEqualTo((short) 64);
        assertThat(pixel.a()).isEqualTo((short) 255);
    }

    @Test
    void readRawImageDataCelChunkGrayscale() throws IOException {
        var baos = new ByteArrayOutputStream();
        // WORD layerIndex = 0
        baos.write(new byte[]{0x00, 0x00});
        // SHORT xPosition = 0
        baos.write(new byte[]{0x00, 0x00});
        // SHORT yPosition = 0
        baos.write(new byte[]{0x00, 0x00});
        // BYTE opacityLevel = 255
        baos.write(new byte[]{(byte) 0xFF});
        // WORD celType = 0 (RAW_IMAGE_DATA)
        baos.write(new byte[]{0x00, 0x00});
        // SHORT zIndex = 0
        baos.write(new byte[]{0x00, 0x00});
        // 5 bytes reserved (future)
        baos.write(new byte[5]);
        // WORD widthInPixels = 1
        baos.write(new byte[]{0x01, 0x00});
        // WORD heightInPixels = 1
        baos.write(new byte[]{0x01, 0x00});
        // 1 pixel in Grayscale color depth (2 bytes: value, alpha)
        baos.write(new byte[]{(byte) 0x80, (byte) 0xFF});

        var reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
        // chunkSize = header (16) + width(2) + height(2) + pixels(2) = 22
        var chunk = CelChunk.build(reader, 22, ColorDepth.Grayscale);

        assertThat(chunk).isInstanceOf(CelChunk.RawImageDataCelChunk.class);
        var rawChunk = (CelChunk.RawImageDataCelChunk) chunk;
        assertThat(rawChunk.pixelData()).hasLength(1);
        var pixel = (Pixel.Grayscale) rawChunk.pixelData()[0];
        assertThat(pixel.value()).isEqualTo((short) 128);
        assertThat(pixel.alpha()).isEqualTo((short) 255);
    }
}
