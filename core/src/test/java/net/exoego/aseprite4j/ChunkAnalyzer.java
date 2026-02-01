package net.exoego.aseprite4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ChunkAnalyzer {
    public static void main(String[] args) throws Exception {
        var resourceDir = Paths.get("core/src/test/resources/aseprite/sprites");
        var files = Files.list(resourceDir)
                .filter(p -> p.toString().endsWith(".aseprite"))
                .sorted()
                .toList();

        Map<String, Set<String>> chunkToFiles = new TreeMap<>();

        for (Path file : files) {
            try {
                var aseFile = AsepriteFile.read(file);
                String fileName = file.getFileName().toString().replace(".aseprite", "");

                for (Frame frame : aseFile.frames()) {
                    for (FrameChunk chunk : frame.chunks()) {
                        String chunkType = chunk.getClass().getSimpleName();

                        // Handle inner classes (e.g., CelChunk$CompressedImageCelChunk)
                        if (chunkType.contains("$")) {
                            chunkType = chunkType.substring(chunkType.lastIndexOf("$") + 1);
                        }

                        chunkToFiles.computeIfAbsent(chunkType, k -> new TreeSet<>()).add(fileName);
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed to read: " + file + " - " + e.getMessage());
            }
        }

        System.out.println("=== Chunk Usage Analysis ===\n");

        for (var entry : chunkToFiles.entrySet()) {
            System.out.println(entry.getKey() + ":");
            System.out.println("  Files: " + String.join(", ", entry.getValue()));
            System.out.println("  Count: " + entry.getValue().size());
            System.out.println();
        }

        System.out.println("\n=== For ParameterizedTest ===\n");

        for (var entry : chunkToFiles.entrySet()) {
            String chunkName = entry.getKey();
            System.out.println("// " + chunkName);
            System.out.println("@ParameterizedTest");
            System.out.println("@ValueSource(strings = {");
            String values = entry.getValue().stream()
                    .map(s -> "        \"" + s + "\"")
                    .collect(Collectors.joining(",\n"));
            System.out.println(values);
            System.out.println("})");
            System.out.println("void read" + chunkName + "(String filename) throws Exception { ... }");
            System.out.println();
        }
    }
}
