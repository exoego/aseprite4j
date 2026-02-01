package net.exoego.aseprite4j.viewer;

import net.exoego.aseprite4j.*;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class AsepriteRenderer {

    public static BufferedImage render(AsepriteFile file, int frameIndex) {
        var header = file.header();
        int width = header.imageWidth();
        int height = header.imageHeight();
        var colorDepth = header.colorDepth();
        int transparentIndex = header.transparentColorIndex();

        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        var frames = file.frames();
        if (frameIndex < 0 || frameIndex >= frames.size()) {
            return image;
        }

        var palette = findPalette(frames);
        var layers = findLayers(frames);

        var frame = frames.get(frameIndex);
        var cels = collectCels(frame, frames);

        for (var cel : cels) {
            renderCel(image, cel, colorDepth, palette, transparentIndex, layers);
        }

        return image;
    }

    public static BufferedImage render(AsepriteFile file) {
        return render(file, 0);
    }

    private static PaletteChunk findPalette(List<Frame> frames) {
        for (var frame : frames) {
            for (var chunk : frame.chunks()) {
                if (chunk instanceof PaletteChunk palette) {
                    return palette;
                }
            }
        }
        return null;
    }

    private static List<LayerChunk> findLayers(List<Frame> frames) {
        var layers = new ArrayList<LayerChunk>();
        for (var frame : frames) {
            for (var chunk : frame.chunks()) {
                if (chunk instanceof LayerChunk layer) {
                    layers.add(layer);
                }
            }
        }
        return layers;
    }

    private record CelData(int layerIndex, int x, int y, int width, int height, Pixel[] pixels) {}

    private static List<CelData> collectCels(Frame frame, List<Frame> allFrames) {
        var cels = new ArrayList<CelData>();

        for (var chunk : frame.chunks()) {
            switch (chunk) {
                case CelChunk.RawImageDataCelChunk cel -> cels.add(new CelData(
                        cel.layerIndex(), cel.xPosition(), cel.yPosition(),
                        cel.widthInPixels(), cel.heightInPixels(), cel.pixelData()));
                case CelChunk.CompressedImageCelChunk cel -> cels.add(new CelData(
                        cel.layerIndex(), cel.xPosition(), cel.yPosition(),
                        cel.widthInPixels(), cel.heightInPixels(), cel.pixelData()));
                case CelChunk.LinkedCelCelChunk cel -> {
                    int linkedFrameIndex = cel.framePositionToLinkWith();
                    if (linkedFrameIndex >= 0 && linkedFrameIndex < allFrames.size()) {
                        var linkedCel = findCelForLayer(allFrames.get(linkedFrameIndex), cel.layerIndex());
                        if (linkedCel != null) {
                            cels.add(new CelData(cel.layerIndex(), cel.xPosition(), cel.yPosition(),
                                    linkedCel.width(), linkedCel.height(), linkedCel.pixels()));
                        }
                    }
                }
                default -> {}
            }
        }

        return cels.stream().sorted((a, b) -> Integer.compare(a.layerIndex(), b.layerIndex())).toList();
    }

    private static CelData findCelForLayer(Frame frame, int layerIndex) {
        for (var chunk : frame.chunks()) {
            switch (chunk) {
                case CelChunk.RawImageDataCelChunk cel when cel.layerIndex() == layerIndex ->
                    { return new CelData(cel.layerIndex(), cel.xPosition(), cel.yPosition(),
                            cel.widthInPixels(), cel.heightInPixels(), cel.pixelData()); }
                case CelChunk.CompressedImageCelChunk cel when cel.layerIndex() == layerIndex ->
                    { return new CelData(cel.layerIndex(), cel.xPosition(), cel.yPosition(),
                            cel.widthInPixels(), cel.heightInPixels(), cel.pixelData()); }
                default -> {}
            }
        }
        return null;
    }

    private static void renderCel(BufferedImage image, CelData cel, ColorDepth colorDepth,
                                  PaletteChunk palette, int transparentIndex, List<LayerChunk> layers) {
        if (cel.layerIndex() < layers.size()) {
            var layer = layers.get(cel.layerIndex());
            if (!layer.flagsSet().contains(LayerFlag.VISIBLE)) {
                return;
            }
        }

        for (int py = 0; py < cel.height(); py++) {
            for (int px = 0; px < cel.width(); px++) {
                int pixelIndex = py * cel.width() + px;
                if (pixelIndex >= cel.pixels().length) continue;

                var pixel = cel.pixels()[pixelIndex];
                int argb = pixelToArgb(pixel, palette, transparentIndex);

                int imgX = cel.x() + px;
                int imgY = cel.y() + py;

                if (imgX >= 0 && imgX < image.getWidth() && imgY >= 0 && imgY < image.getHeight()) {
                    int alpha = (argb >>> 24) & 0xFF;
                    if (alpha > 0) {
                        int existingArgb = image.getRGB(imgX, imgY);
                        int blended = blendPixels(existingArgb, argb);
                        image.setRGB(imgX, imgY, blended);
                    }
                }
            }
        }
    }

    private static int pixelToArgb(Pixel pixel, PaletteChunk palette, int transparentIndex) {
        return switch (pixel) {
            case Pixel.RGBA p -> {
                int r = p.r() & 0xFF;
                int g = p.g() & 0xFF;
                int b = p.b() & 0xFF;
                int a = p.a() & 0xFF;
                yield (a << 24) | (r << 16) | (g << 8) | b;
            }
            case Pixel.Grayscale p -> {
                int v = p.value() & 0xFF;
                int a = p.alpha() & 0xFF;
                yield (a << 24) | (v << 16) | (v << 8) | v;
            }
            case Pixel.Index p -> {
                int index = p.index() & 0xFF;
                if (index == transparentIndex) {
                    yield 0x00000000;
                } else if (palette != null && index < palette.entries().size()) {
                    var color = palette.entries().get(index).color();
                    int r = color.r() & 0xFF;
                    int g = color.g() & 0xFF;
                    int b = color.b() & 0xFF;
                    int a = color.a() & 0xFF;
                    yield (a << 24) | (r << 16) | (g << 8) | b;
                } else {
                    yield 0xFF000000 | (index << 16) | (index << 8) | index;
                }
            }
            default -> 0x00000000;
        };
    }

    private static int blendPixels(int background, int foreground) {
        int fgAlpha = (foreground >>> 24) & 0xFF;
        if (fgAlpha == 255) return foreground;
        if (fgAlpha == 0) return background;

        int bgAlpha = (background >>> 24) & 0xFF;
        if (bgAlpha == 0) return foreground;

        int fgR = (foreground >>> 16) & 0xFF;
        int fgG = (foreground >>> 8) & 0xFF;
        int fgB = foreground & 0xFF;

        int bgR = (background >>> 16) & 0xFF;
        int bgG = (background >>> 8) & 0xFF;
        int bgB = background & 0xFF;

        int outAlpha = fgAlpha + bgAlpha * (255 - fgAlpha) / 255;
        if (outAlpha == 0) return 0;

        int outR = (fgR * fgAlpha + bgR * bgAlpha * (255 - fgAlpha) / 255) / outAlpha;
        int outG = (fgG * fgAlpha + bgG * bgAlpha * (255 - fgAlpha) / 255) / outAlpha;
        int outB = (fgB * fgAlpha + bgB * bgAlpha * (255 - fgAlpha) / 255) / outAlpha;

        return (outAlpha << 24) | (outR << 16) | (outG << 8) | outB;
    }
}
