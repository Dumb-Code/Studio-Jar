package net.dumbcode.studio.model;

public enum ModelMirror {
    NONE(),
    X(0),
    XY(0, 1),
    XZ(0, 2),
    XYZ(0, 1, 2),

    Y(1),
    YZ(1, 2),

    Z(2);

    public static ModelMirror global = NONE;

    private final float[][] planeNormals;

    private final float[] origin = new float[3];

    ModelMirror(int... normals) {
        this.planeNormals = new float[normals.length][3];
        for (int i = 0; i < normals.length; i++) {
            this.planeNormals[i] = new float[] { normals[i]==0?1:0, normals[i]==1?1:0, normals[i]==2?1:0};
        }
    }

    public void applyAsGlobal(float rootX, float rootY, float rootZ) {
        global = this;
        origin[0] = rootX;
        origin[1] = rootY;
        origin[2] = rootZ;
    }

    public float[] getOrigin() {
        return origin;
    }

    public float[][] getPlaneNormals() {
        return planeNormals;
    }
}
