package net.exoego.aseprite4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class UserDataChunk implements FrameChunk {

    private static final UserDataChunk EMPTY = new UserDataChunk(null, null, null);

    private final String maybeText;
    private final Color.RGBA maybeColor;
    private final Map<String, Object> maybeProperties;

    public UserDataChunk(String maybeText, Color.RGBA maybeColor, Map<String, Object> maybeProperties) {
        this.maybeText = maybeText;
        this.maybeColor = maybeColor;
        this.maybeProperties = maybeProperties;
    }

    public Optional<String> getMaybeText() {
        return Optional.ofNullable(this.maybeText);
    }

    public Optional<Color.RGBA> getMaybeColor() {
        return Optional.ofNullable(this.maybeColor);
    }

    public Optional<Map<String, Object>> getMaybeProperties() {
        return Optional.ofNullable(this.maybeProperties);
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

        if (!Objects.equals(maybeText, that.maybeText)) return false;
        if (!Objects.equals(maybeColor, that.maybeColor)) return false;
        return Objects.equals(maybeProperties, that.maybeProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maybeText, maybeColor, maybeProperties);
    }

    static UserDataChunk build(int chunkSize, InputStreamReader reader) throws IOException {
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
            var numberOfPropertyMaps = Math.toIntExact(reader.DWORD());
            maybeProperties = new HashMap<>();

            for (int mapIndex = 0; mapIndex < numberOfPropertyMaps; mapIndex++) {
                var extensionKey = reader.DWORD();
                var numberOfProperties = Math.toIntExact(reader.DWORD());

                for (int i = 0; i < numberOfProperties; i++) {
                    var propertyName = reader.STRING();
                    var propertyValue = readPropertyValue(reader);
                    maybeProperties.put(propertyName, propertyValue);
                }
            }
        }

        return new UserDataChunk(maybeText, maybeColor, maybeProperties);
    }

    private static Object readPropertyValue(InputStreamReader reader) throws IOException {
        var propertyType = reader.WORD();
        return switch (propertyType) {
            case 0x01 -> reader.BYTE() != 0; // bool
            case 0x02 -> (byte) reader.BYTE(); // int8
            case 0x03 -> reader.BYTE(); // uint8
            case 0x04 -> (short) reader.SHORT(); // int16
            case 0x05 -> reader.WORD(); // uint16
            case 0x06 -> reader.INT32(); // int32
            case 0x07 -> reader.DWORD(); // uint32
            case 0x08 -> reader.INT64(); // int64
            case 0x09 -> reader.QWORD(); // uint64
            case 0x0A -> reader.FIXED(); // fixed
            case 0x0B -> reader.FLOAT(); // float
            case 0x0C -> reader.DOUBLE(); // double
            case 0x0D -> reader.STRING(); // string
            case 0x0E -> reader.POINT(); // point
            case 0x0F -> reader.SIZE(); // size
            case 0x10 -> reader.RECT(); // rect
            case 0x11 -> readVector(reader); // vector
            case 0x12 -> readNestedProperties(reader); // nested properties map
            case 0x13 -> reader.UUID(); // uuid
            default -> throw new IOException("Unknown property type: " + propertyType);
        };
    }

    private static List<Object> readVector(InputStreamReader reader) throws IOException {
        var numberOfElements = Math.toIntExact(reader.DWORD());
        var elementType = reader.WORD();
        var list = new ArrayList<>(numberOfElements);

        if (elementType == 0) {
            // All elements are not of the same type
            for (int i = 0; i < numberOfElements; i++) {
                var element = readPropertyValue(reader);
                list.add(element);
            }
        } else {
            // All elements are of the same type
            for (int i = 0; i < numberOfElements; i++) {
                var element = readPropertyValueOfType(reader, elementType);
                list.add(element);
            }
        }

        return list;
    }

    private static Object readPropertyValueOfType(InputStreamReader reader, int propertyType) throws IOException {
        return switch (propertyType) {
            case 0x01 -> reader.BYTE() != 0; // bool
            case 0x02 -> (byte) reader.BYTE(); // int8
            case 0x03 -> reader.BYTE(); // uint8
            case 0x04 -> (short) reader.SHORT(); // int16
            case 0x05 -> reader.WORD(); // uint16
            case 0x06 -> reader.INT32(); // int32
            case 0x07 -> reader.DWORD(); // uint32
            case 0x08 -> reader.INT64(); // int64
            case 0x09 -> reader.QWORD(); // uint64
            case 0x0A -> reader.FIXED(); // fixed
            case 0x0B -> reader.FLOAT(); // float
            case 0x0C -> reader.DOUBLE(); // double
            case 0x0D -> reader.STRING(); // string
            case 0x0E -> reader.POINT(); // point
            case 0x0F -> reader.SIZE(); // size
            case 0x10 -> reader.RECT(); // rect
            case 0x11 -> readVector(reader); // nested vector
            case 0x12 -> readNestedProperties(reader); // nested properties map
            case 0x13 -> reader.UUID(); // uuid
            default -> throw new IOException("Unknown property type: " + propertyType);
        };
    }

    private static Map<String, Object> readNestedProperties(InputStreamReader reader) throws IOException {
        var numberOfProperties = Math.toIntExact(reader.DWORD());
        var map = new HashMap<String, Object>(numberOfProperties);

        for (int i = 0; i < numberOfProperties; i++) {
            var propertyName = reader.STRING();
            var propertyValue = readPropertyValue(reader);
            map.put(propertyName, propertyValue);
        }

        return map;
    }
}
