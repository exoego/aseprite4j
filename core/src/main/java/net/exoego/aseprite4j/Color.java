package net.exoego.aseprite4j;

public interface Color {
    record RGBA(short r, short g, short b, short a) implements Color {
        @Override
        public String toString() {
            // In #rrggbbaa format
            return String.format("#%02x%02x%02x:%02x", r & 0xFF, g & 0xFF, b & 0xFF, a & 0xFF);
        }

    }

    record RGB(short r, short g, short b) implements Color {
        @Override
        public String toString() {
            // In #rrggbb format
            return String.format("#%02x%02x%02x", r & 0xFF, g & 0xFF, b & 0xFF);
        }
    }
}
