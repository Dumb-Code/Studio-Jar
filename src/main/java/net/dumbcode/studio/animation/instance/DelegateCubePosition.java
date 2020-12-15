package net.dumbcode.studio.animation.instance;

public class DelegateCubePosition {
    private final AnimatedCube cubeReference;
    private final float[] defaultPosition = new float[3];
    private final float[] position = new float[3];

    public DelegateCubePosition(AnimatedCube cubeReference) {
        this.cubeReference = cubeReference;
        System.arraycopy(cubeReference.getInfo().getRotationPoint(), 0, this.defaultPosition, 0, 3);
    }

    public void apply() {
        this.cubeReference.setPosition(this.position[0], this.position[1], this.position[2]);
    }

    public void reset() {
        System.arraycopy(this.defaultPosition, 0, this.position, 0, 3);
    }

    public void addPosition(float x, float y, float z) {
        this.position[0] += x;
        this.position[1] += y;
        this.position[2] += z;
    }
}
