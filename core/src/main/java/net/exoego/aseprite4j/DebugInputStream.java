package net.exoego.aseprite4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;

final class DebugInputStream extends InputStream {
    private final InputStream in;

    private final ArrayDeque<Integer> buffer = new ArrayDeque<>();

    private int prevBeginAddress;
    private int prevEndAddress;

    DebugInputStream(InputStream in) {
        this.in = in;
    }

    @Override
    public int read() throws IOException {
        try {
            int b = this.in.read();
            buffer.add(b);
            return b;
        } catch (IOException e) {
            throw new IOException("Failed to read from input stream: " + this.currentAddress());
        }
    }

    @Override
    public void skipNBytes(long n) throws IOException {
        try {
            super.skipNBytes(n);
        } catch (IOException e) {
            throw new IOException(
                    "Failed to skip " + n + " bytes from input stream at\n" + this.currentAddress(), e);
        }
    }

    public String currentAddress() {
        var builder = new StringBuilder();
        var count = 0;
        var line = 0;

        if (prevEndAddress == 0) {
            builder.append("initial read\n");
        } else {
            builder.append(String.format("last read %08X - %08X (%d bytes)\n",
                    prevBeginAddress, prevEndAddress - 1,
                    prevEndAddress - prevBeginAddress
            ));
        }

        builder.append("Address   00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F\n");

        prevBeginAddress = prevEndAddress;
        for (Integer integer : buffer) {
            if (count == 0) {
                builder.append(String.format("%08X: ", line * 16));
            }
            if (prevEndAddress == count + line * 16) {
                prevEndAddress++;
            }
            var b1 = integer;
            count++;
            builder.append(String.format("%02X ", (short) (0xFF & b1)));
            if (count == 16) {
                builder.append('\n');
                count = 0;
                line++;
            }
        }

        builder.append('\n');
        return builder.toString();
    }
}
