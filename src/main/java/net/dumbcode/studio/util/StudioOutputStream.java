package net.dumbcode.studio.util;

import java.io.*;

public class StudioOutputStream {

    private final DataOutputStream stream;

    public StudioOutputStream(OutputStream stream) {
        this.stream = new DataOutputStream(stream);
    }

    public void writeFloat(float f) throws IOException {
        this.stream.writeFloat(f);
    }

    public void writeFloatArray(float[] arr, int size) throws IOException {
        for (int i = 0; i < size; i++) {
            this.writeFloat(arr[i]);
        }
    }

    public void writeInt(int i) throws IOException {
        this.writeFloat(i);
    }

    public void writeIntArray(int[] arr, int size) throws IOException {
        for (int i = 0; i < size; i++) {
            this.writeInt(arr[i]);
        }
    }

    public void writeBoolean(boolean b) throws IOException {
        this.stream.writeBoolean(b);
    }

    public void writeString(String str) throws IOException {
        this.stream.writeUTF(str);
    }
}
