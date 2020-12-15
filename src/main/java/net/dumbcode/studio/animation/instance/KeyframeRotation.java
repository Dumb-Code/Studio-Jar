package net.dumbcode.studio.animation.instance;

import net.dumbcode.studio.model.RotationOrder;
import net.dumbcode.studio.util.RotationReorder;

import java.util.EnumMap;
import java.util.Map;

public class KeyframeRotation {
    private final Map<RotationOrder, float[]> rotationOrderMap = new EnumMap<>(RotationOrder.class);

    public KeyframeRotation(float[] angles, RotationOrder order) {
        this.rotationOrderMap.put(order, angles);
        for (RotationOrder value : RotationOrder.values()) {
            if(value == order) {
                continue;
            }
            float[] arr = new float[] { angles[0], angles[1], angles[2] };
            RotationReorder.reorder(arr, order, value);
            this.rotationOrderMap.put(value, angles);
        }

    }
}
