# aseprite4j

[![test](https://github.com/exoego/aseprite4j/actions/workflows/CI.yml/badge.svg)](https://github.com/exoego/aseprite4j/actions/workflows/CI.yml)
[![codecov](https://codecov.io/gh/exoego/aseprite4j/graph/badge.svg?token=pRNJUDCRIY)](https://codecov.io/gh/exoego/aseprite4j)
[![Maven Central](https://img.shields.io/maven-central/v/net.exoego.aseprite4j/aseprite4j-core)](https://central.sonatype.com/artifact/net.exoego.aseprite4j/aseprite4j-core)

A Java library for reading [Aseprite](https://www.aseprite.org/) files (.aseprite/.ase).

## Requirements

- Java 21 or later

## Installation

### Maven

```xml
<dependency>
    <groupId>net.exoego.aseprite4j</groupId>
    <artifactId>aseprite4j-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle (Kotlin DSL)

```kotlin
implementation("net.exoego.aseprite4j:aseprite4j-core:1.0.0")
```

### SBT

```scala
libraryDependencies += "net.exoego.aseprite4j" % "aseprite4j-core" % "1.0.0"
```

## Usage

### Reading an Aseprite file

```java
import net.exoego.aseprite4j.AsepriteFile;
import java.nio.file.Path;

AsepriteFile file = AsepriteFile.read(Path.of("sprite.aseprite"));

var header = file.header();
System.out.println("Image size: " + header.imageWidth() + "x" + header.imageHeight());
System.out.println("Number of frames: " + header.numberOfFrames());
System.out.println("Color depth: " + header.colorDepth());

for (var frame : file.frames()) {
    System.out.println("Frame duration: " + frame.header().frameDurationInMs() + "ms");
    for (var chunk : frame.chunks()) {
        // Process chunks (layers, cels, palette, etc.)
    }
}
```

### Reading only the header

If you only need metadata without loading the entire file:

```java
import net.exoego.aseprite4j.Header;
import java.nio.file.Path;

Header header = Header.read(Path.of("sprite.aseprite"));
System.out.println("Image size: " + header.imageWidth() + "x" + header.imageHeight());
```

## License

Apache License 2.0
