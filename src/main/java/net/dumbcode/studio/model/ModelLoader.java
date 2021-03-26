package net.dumbcode.studio.model;

import net.dumbcode.studio.util.ModelMirrorApplier;
import net.dumbcode.studio.util.StudioInputStream;
import net.dumbcode.studio.util.RotationReorder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class ModelLoader {

    public static final int MINIMUM_VERSION = 2;
    public static final int MAXIMUM_VERSION = 2;

    public static ModelInfo loadModel(InputStream stream) throws IOException {
        return loadModel(stream, RotationOrder.global, ModelMirror.global);
    }
    public static ModelInfo loadModel(InputStream stream, RotationOrder order, ModelMirror mirrorOrder) throws IOException {
        Objects.requireNonNull(order, "Rotation Order is null");

        StudioInputStream buffer = new StudioInputStream(stream);
        RotationOrder current = RotationOrder.ZYX; //TODO: part of the format.

        int version = buffer.readInt();

        if(version < MINIMUM_VERSION) {
            throw new IOException("Model Needs to be at least version: " + MINIMUM_VERSION + ". Got:" + version);
        }
        if(version > MAXIMUM_VERSION) {
            throw new IOException("Animation is too advanced. Please update studio jar. Maximum supported:" + MAXIMUM_VERSION + ". Got:" + version);
        }

        ModelInfo info = new ModelInfo(version, buffer.readString(), buffer.readInt(), buffer.readInt(), order);
        readCubeArray(info, buffer, current, order, info.getRoots());

        if(mirrorOrder != ModelMirror.NONE) {
            ModelMirrorApplier.mirrorCubes(info.getRoots(), mirrorOrder);
        }
        return info;
    }

    private static void readCubeArray(ModelInfo parent, StudioInputStream buffer, RotationOrder current, RotationOrder target, List<CubeInfo> list) throws IOException {
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            CubeInfo info = new CubeInfo(
                parent,
                buffer.readString(),
                buffer.readIntArray(3),
                buffer.readFloatArray(3),
                buffer.readFloatArray(3),
                buffer.readFloatArray(3),
                buffer.readIntArray(2),
                buffer.readBoolean(),
                buffer.readFloatArray(3),
                current
            );
            RotationReorder.reorder(info.getRotation(), current, target);
            readCubeArray(parent, buffer, current, target, info.getChildren());
            list.add(info);
        }
    }

}
