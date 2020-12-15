package com.dumbcodemc.studio.animation.instance;

public class DelegateCube {

    private final AnimatedCube cubeReference;

    private final float[] defaultRotation = new float[3];
    private final float[] defaultPosition = new float[3];

    private final float[] rotation = new float[3];
    private final float[] position = new float[3];

    public DelegateCube(AnimatedCube cubeReference) {
        this.cubeReference = cubeReference;
        System.arraycopy(cubeReference.getInfo().getRotation(), 0, this.defaultRotation, 0, 3);
        System.arraycopy(cubeReference.getInfo().getRotationPoint(), 0, this.defaultPosition, 0, 3);
    }

    public void apply() {
        this.cubeReference.setRotation(this.rotation[0], this.rotation[1], this.rotation[2]);
        this.cubeReference.setPosition(this.position[0], this.position[1], this.position[2]);
    }

    public void reset() {
        System.arraycopy(this.defaultRotation, 0, this.rotation, 0, 3);
        System.arraycopy(this.defaultPosition, 0, this.position, 0, 3);
    }

    public void addRotation(float x, float y, float z) {
        this.rotation[0] += x;
        this.rotation[1] += y;
        this.rotation[2] += z;
    }

    public void addPosition(float x, float y, float z) {
        this.position[0] += x;
        this.position[1] += y;
        this.position[2] += z;
    }
}
