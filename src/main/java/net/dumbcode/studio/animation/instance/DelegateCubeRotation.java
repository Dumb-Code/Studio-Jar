package net.dumbcode.studio.animation.instance;

import net.dumbcode.studio.model.RotationOrder;
import net.dumbcode.studio.util.RotationReorder;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

public class DelegateCubeRotation {

    private final AnimatedCube cubeReference;
    private final RotationOrder modelOrder;

    private final Map<RotationOrder, float[]> defaultMap = new EnumMap<>(RotationOrder.class);
    private final Map<RotationOrder, float[]> rotationMap = new EnumMap<>(RotationOrder.class);

    public DelegateCubeRotation(AnimatedCube cubeReference, RotationOrder modelOrder) {
        this.cubeReference = cubeReference;
        this.modelOrder = modelOrder;

        this.defaultMap.put(modelOrder, Arrays.copyOf(cubeReference.getInfo().getRotation(), 3));
        for (RotationOrder value : RotationOrder.values()) {
            if(value == modelOrder) {
                continue;
            }
            this.defaultMap.put(value, RotationReorder.reorder(Arrays.copyOf(this.defaultMap.get(modelOrder), 3), modelOrder, value));
        }
    }

    public void apply() {
        float[] modelD = this.defaultMap.get(this.modelOrder);
        float[] arr = Arrays.copyOf(modelD, 3);
        for (RotationOrder value : RotationOrder.values()) {
            float[] d = this.defaultMap.get(value);
            float[] a = this.rotationMap.get(value);

            if(value == this.modelOrder) {
                arr[0] += a[0];
                arr[1] += a[1];
                arr[2] += a[2];
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
    }

    public void reset() {
        for (RotationOrder value : RotationOrder.values()) {
            this.rotationMap.put(value, new float[3]);
        }
    }

    public void addRotation(RotationOrder order, float x, float y, float z) {
        float[] arr = this.rotationMap.get(order);
        arr[0] += x;
        arr[1] += y;
        arr[2] += z;
    }
}
