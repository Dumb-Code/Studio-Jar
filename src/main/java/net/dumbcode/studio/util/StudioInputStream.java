package net.dumbcode.studio.util;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StudioInputStream {

    private final DataInputStream stream;

    public StudioInputStream(InputStream stream) {
        this.stream = new DataInputStream(stream);
    }

    public float readFloat() throws IOException {
        return this.stream.readFloat();
    }
    
    public float[] readFloatArray(int size) throws IOException {
        return readFloatArray(size, 1);
    }

    public float[] readFloatArray(int size, float modifier) throws IOException {
        float[] out = new float[size];
        for (int i = 0; i < out.length; i++) {
            out[i] = this.readFloat()*modifier;
        }
        return out;
    }

    public int readInt() throws IOException {
        return (int) this.readFloat();
    }

    public int[] readIntArray(int size) throws IOException {
        int[] out = new int[size];
        for (int i = 0; i < out.length; i++) {
            out[i] = this.readInt();
        }
        return out;
    }

    public boolean readBoolean() throws IOException {
        return this.stream.readBoolean();
    }

    public String readString() throws IOException {
        return this.stream.readUTF();
    }
}
