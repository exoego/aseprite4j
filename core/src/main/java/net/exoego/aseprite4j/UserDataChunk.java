package net.exoego.aseprite4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class UserDataChunk implements FrameChunk {

    private static UserDataChunk EMPTY = new UserDataChunk(null, null, null);

    private final String maybeText;
    private final Color.RGBA maybeColor;
    private final Map<String, Object> maybeProperties;

    public UserDataChunk(String maybeText, Color.RGBA maybeColor, Map<String, Object> maybeProperties) {
        this.maybeText = maybeText;
        this.maybeColor = maybeColor;
        this.maybeProperties = maybeProperties;
    }

    public Optional<String> getMaybeText() {
        return Optional.of(this.maybeText);
    }

    public Optional<Color.RGBA> getMaybeColor() {
        return Optional.of(this.maybeColor);
    }

    public Optional<Map<String, Object>> getMaybeProperties() {
        return Optional.of(this.maybeProperties);
    }

    @Override
    public String toString() {
        return "UserDataChunk[" +
                "maybeText='" + maybeText + '\'' +
                ", maybeColor=" + maybeColor +
                ", maybeProperties=" + maybeProperties +
                ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDataChunk that)) return false;

        if (!maybeText.equals(that.maybeText)) return false;
        if (!maybeColor.equals(that.maybeColor)) return false;
        return maybeProperties.equals(that.maybeProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maybeText, maybeColor, maybeProperties);
    }

    static UserDataChunk build(int chunkSize, InputStreamReader reader) throws IOException {
        return reader.checkSize(chunkSize, () -> {
            var flags = reader.DWORD();

            if (flags == 0) {
                // empty user data chunk may appear after the tileset chunk
                return EMPTY;
            }

            String maybeText = null;
            if ((flags & 0x1) != 0) {
                maybeText = reader.STRING();
            }

            Color.RGBA maybeColor = null;
            if ((flags & 0x2) != 0) {
                maybeColor = new Color.RGBA(reader.BYTE(), reader.BYTE(), reader.BYTE(), reader.BYTE());
            }

            Map<String, Object> maybeProperties = null;

            if ((flags & 0x4) != 0) {
                var sizeInBytesOfAllPropertiesInThisChunk = reader.DWORD();
                var numberOfProperties = Math.toIntExact(reader.DWORD());
                maybeProperties = new HashMap<String, Object>(numberOfProperties);
                for (int i = 0; i < numberOfProperties; i++) {
                    var propertyName = reader.STRING();
                    if (propertyName.isEmpty()) {
                        // user properties
                    }
                    var propertyType = reader.WORD();
                    switch (propertyType) {
                        case 0x1 -> {
                            var bool = reader.BYTE() != 0;
                            maybeProperties.put(propertyName, bool);
                        }
                        case 0x2 -> {
                            var int8 = reader.BYTE();
                            maybeProperties.put(propertyName, int8);
                        }
                        case 0x3 -> {
                            var uint8 = reader.BYTE();
                            maybeProperties.put(propertyName, uint8);
                        }
                        case 0x4 -> {
                            var int16 = reader.SHORT();
                            maybeProperties.put(propertyName, int16);
                        }
                        case 0x5 -> {
                            var uint16 = reader.WORD();
                            maybeProperties.put(propertyName, uint16);
                        }
                        case 0x6 -> {
                            var int32 = reader.INT32();
                            maybeProperties.put(propertyName, int32);
                        }
                        case 0x7 -> {
                            var uint32 = reader.DWORD();
                            maybeProperties.put(propertyName, uint32);
                        }
                        case 0x8 -> {
                            var int64 = reader.INT64();
                            maybeProperties.put(propertyName, int64);
                        }
                        case 0x9 -> {
                            var uint64 = reader.QWORD();
                            maybeProperties.put(propertyName, uint64);
                        }
                        case 0xA -> {
                            var v = reader.FIXED();
                            maybeProperties.put(propertyName, v);
                        }
                        case 0xB -> {
                            var v = reader.FLOAT();
                            maybeProperties.put(propertyName, v);
                        }
                        case 0xC -> {
                            var v = reader.DOUBLE();
                            maybeProperties.put(propertyName, v);
                        }
                        case 0xD -> {
                            var v = reader.STRING();
                            maybeProperties.put(propertyName, v);
                        }
                        case 0xE -> {
                            var v = reader.POINT();
                            maybeProperties.put(propertyName, v);
                        }
                        case 0xF -> {
                            var v = reader.SIZE();
                            maybeProperties.put(propertyName, v);
                        }
                        case 0x10 -> {
                            var v = reader.RECT();
                            maybeProperties.put(propertyName, v);
                        }
                        case 0x11 -> {
                            // TODO:vector
                        }
                        case 0x12 -> {
                            // TODO:nested
                        }
                        case 0x13 -> {
                            var v = reader.UUID();
                            maybeProperties.put(propertyName, v);
                        }
                        default -> {
                            System.out.println("Unknown property type: " + propertyType + " for property: " + propertyName);
                        }
                    }
                }

                if (maybeProperties.size() != numberOfProperties) {
                    throw new IllegalStateException("Expected " + numberOfProperties + " properties, but got " + maybeProperties.size());
                }
            }

            return new UserDataChunk(maybeText, maybeColor, maybeProperties);
        });
    }
}
