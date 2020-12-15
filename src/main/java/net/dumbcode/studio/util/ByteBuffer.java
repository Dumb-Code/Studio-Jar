package net.dumbcode.studio.util;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteBuffer {

    private final DataInputStream stream;

    public ByteBuffer(InputStream stream) {
        this.stream = new DataInputStream(stream);
    }

    public float readFloat() throws IOException {
        return this.stream.readFloat();
    }

    public int readInt() throws IOException {
        return (int) this.readFloat();
    }

    public boolean readBoolean() throws IOException {
        return this.stream.readBoolean();
    }

    public String readString() throws IOException {
        return this.stream.readUTF();
    }
}
