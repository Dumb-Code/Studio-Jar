package net.dumbcode.studio.animation.instance;

import net.dumbcode.studio.model.CubeInfo;
import net.dumbcode.studio.model.RotationOrder;
import net.dumbcode.studio.util.RotationReorder;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

public class DelegateCube {

    private final Map<RotationOrder, float[]> rotationMap = new EnumMap<>(RotationOrder.class);
    private final float[] position = new float[3];
    private final float[] cubeDims = new float[3];

    public void apply(AnimatedCube cube) {
        CubeInfo info = cube.getInfo();
        float[] modelD = info.getRotationFor(info.getRotationOrder());
        float[] arr = Arrays.copyOf(modelD, 3);
        for (RotationOrder value : RotationOrder.values()) {
            float[] d = info.getRotationFor(value);
            float[] a = this.rotationMap.get(value);

            if (value == info.getRotationOrder()) {
                arr[0] += a[0];
                arr[1] += a[1];
                arr[2] += a[2];
                continue;
            }
            if (a[0] == 0 && a[1] == 0 && a[2] == 0) {
                continue;
            }
            a[0] += d[0];
            a[1] += d[1];
            a[2] += d[2];

            RotationReorder.reorder(a, value, info.getRotationOrder());
            arr[0] += a[0] - modelD[0];
            arr[1] += a[1] - modelD[1];
            arr[2] += a[2] - modelD[2];
        }

        cube.setRotation(arr[0], arr[1], arr[2]);

        float[] pos = info.getRotationPoint();
        cube.setPosition(this.position[0]+pos[0], this.position[1]+pos[1], this.position[2]+pos[2]);

        float[] dims = info.getCubeGrow();
        cube.setCubeGrow(this.cubeDims[0]+dims[0], this.cubeDims[1]+dims[1], this.cubeDims[2]+dims[2]);
    }

    public void reset() {
        for (RotationOrder value : RotationOrder.values()) {
            this.rotationMap.put(value, new float[3]);
        }
        for (int i = 0; i < 3; i++) {
            this.position[i] = 0;
            this.cubeDims[i] = 0;
        }
    }

    public void addRotation(RotationOrder order, float x, float y, float z) {
        float[] arr = this.rotationMap.get(order);
        arr[0] += x;
        arr[1] += y;
        arr[2] += z;
    }

    public void addPosition(float x, float y, float z) {
        this.position[0] += x;
        this.position[1] += y;
        this.position[2] += z;
    }

    public void addCubeGrow(float x, float y, float z) {
        this.cubeDims[0] += x;
        this.cubeDims[1] += y;
        this.cubeDims[2] += z;
    }
}
