package net.dumbcode.studio.util;

import net.dumbcode.studio.model.RotationOrder;

public interface EssentiallyACube {
    float[] getRotation(); //In radians
    RotationOrder getRotationOrder();
    float[] getRotationPoint(); //Position
    float[] getCubeGrow();
    float[] getOffset();
    int[] getDimensions();
    EssentiallyACube getParent();
}
