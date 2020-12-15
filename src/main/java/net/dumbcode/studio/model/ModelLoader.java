package net.dumbcode.studio.model;

import net.dumbcode.studio.util.ByteBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ModelLoader {

    public static ModelInfo loadModel(InputStream stream) throws IOException {
        ByteBuffer buffer = new ByteBuffer(stream);
        ModelInfo info = new ModelInfo(buffer.readInt(), buffer.readString(), buffer.readInt(), buffer.readInt());
        readCubeArray(info, buffer, info.getRoots());
        return info;
    }

    private static void readCubeArray(ModelInfo parent, ByteBuffer buffer, List<CubeInfo> list) throws IOException {
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            CubeInfo info = new CubeInfo(
                parent,
                buffer.readString(),
                new int[] { buffer.readInt(), buffer.readInt(), buffer.readInt() },
                new float[] { buffer.readFloat(), buffer.readFloat(), buffer.readFloat() },
                new float[] { buffer.readFloat(), buffer.readFloat(), buffer.readFloat() },
                new float[] { buffer.readFloat(), buffer.readFloat(), buffer.readFloat() },
                new int[] { buffer.readInt(), buffer.readInt() },
                buffer.readBoolean(),
                new float[] { buffer.readFloat(), buffer.readFloat(), buffer.readFloat() }
            );
            readCubeArray(parent, buffer, info.getChildren());
            list.add(info);
        }
    }

}
