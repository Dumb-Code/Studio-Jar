package net.dumbcode.studio.animation.instance;

import net.dumbcode.studio.model.CubeInfo;
import net.dumbcode.studio.model.RotationOrder;
import net.dumbcode.studio.util.RotationReorder;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

public class DelegateCube {

    private final AnimatedCube cubeReference;
    private final RotationOrder modelOrder;

    private final Map<RotationOrder, float[]> defaultRotationMap = new EnumMap<>(RotationOrder.class);
    private final Map<RotationOrder, float[]> rotationMap = new EnumMap<>(RotationOrder.class);

    private final float[] defaultPosition = new float[3];
    private final float[] position = new float[3];

    private final float[] defaultCubeDims = new float[3];
    private final float[] cubeDims = new float[3];

    public DelegateCube(AnimatedCube cubeReference, RotationOrder modelOrder) {
        this.cubeReference = cubeReference;
        this.modelOrder = modelOrder;

        CubeInfo info = cubeReference.getInfo();
        this.defaultRotationMap.put(modelOrder, Arrays.copyOf(info.getRotation(), 3));
        for (RotationOrder value : RotationOrder.values()) {
            if(value == modelOrder) {
                continue;
            }
            this.defaultRotationMap.put(value, RotationReorder.reorder(Arrays.copyOf(this.defaultRotationMap.get(modelOrder), 3), modelOrder, value));
        }

        System.arraycopy(info.getRotationPoint(), 0, this.defaultPosition, 0, 3);
        System.arraycopy(info.getCubeGrow(), 0, this.defaultCubeDims, 0, 3);
    }

    public void apply() {
        float[] modelD = this.defaultRotationMap.get(this.modelOrder);
        float[] arr = Arrays.copyOf(modelD, 3);
        for (RotationOrder value : RotationOrder.values()) {
            float[] d = this.defaultRotationMap.get(value);
            float[] a = this.rotationMap.get(value);

            if (value == this.modelOrder) {
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

            RotationReorder.reorder(a, value, this.modelOrder);
            arr[0] += a[0] - modelD[0];
            arr[1] += a[1] - modelD[1];
            arr[2] += a[2] - modelD[2];
        }
        this.cubeReference.setRotation(arr[0], arr[1], arr[2]);
        this.cubeReference.setPosition(this.position[0], this.position[1], this.position[2]);
        this.cubeReference.setCubeGrow(this.cubeDims[0], this.cubeDims[1], this.cubeDims[2]);
    }

    public void reset() {
        for (RotationOrder value : RotationOrder.values()) {
            this.rotationMap.put(value, new float[3]);
        }
        System.arraycopy(this.defaultPosition, 0, this.position, 0, 3);
        System.arraycopy(this.defaultCubeDims, 0, this.cubeDims, 0, 3);
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
