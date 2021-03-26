package net.dumbcode.studio.model;

import net.dumbcode.studio.util.StudioOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ModelWriter {

    public static void writeModel(ModelInfo info, OutputStream stream) throws IOException {
        StudioOutputStream buffer = new StudioOutputStream(stream);

        buffer.writeInt(2);
        buffer.writeString(info.getAuthor());
        buffer.writeInt(info.getTextureWidth());
        buffer.writeInt(info.getTextureHeight());

        writeCubeArray(buffer, info.getRoots());
    }

    private static void writeCubeArray(StudioOutputStream buffer, List<CubeInfo> list) throws IOException {
        buffer.writeInt(list.size());
        for (CubeInfo info : list) {
            buffer.writeString(info.getName());
            buffer.writeIntArray(info.getDimensions(), 3);
            buffer.writeFloatArray(info.getRotationPoint(), 3);
            buffer.writeFloatArray(info.getOffset(), 3);
            float[] rotation = info.getRotationFor(RotationOrder.ZYX);
            for (int i = 0; i < 3; i++) {
                buffer.writeFloat((float) Math.toDegrees(rotation[i]));
            }
            buffer.writeIntArray(info.getTextureOffset(), 2);
            buffer.writeBoolean(info.isTextureMirrored());
            buffer.writeFloatArray(info.getCubeGrow(), 3);
            writeCubeArray(buffer, info.getChildren());
        }
    }
}
