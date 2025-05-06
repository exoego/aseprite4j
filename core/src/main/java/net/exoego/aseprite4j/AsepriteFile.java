package net.exoego.aseprite4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;

public interface AsepriteFile {
    Header getHeader();


    static AsepriteFile read(InputStream in)throws IOException {
        var buffer =  AsepriteBuffer.from(in);


        var file = new AsepriteFile() {
            @Override
            public Header getHeader() {
                return null;
            }
        };
        return file;
    }
}
