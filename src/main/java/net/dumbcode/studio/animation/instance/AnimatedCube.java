package net.dumbcode.studio.animation.instance;

import net.dumbcode.studio.model.CubeInfo;

public interface AnimatedCube {
    CubeInfo getInfo();
    void setRotation(float x, float y, float z);
    void setPosition(float x, float y, float z);
}
