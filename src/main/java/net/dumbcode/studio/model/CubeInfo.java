package net.dumbcode.studio.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CubeInfo {
    private final ModelInfo model;
    private final String name;
    private final int[] dimensions;
    private final float[] rotationPoint;
    private final float[] offset;
    private final float[] rotation;
    private final int[] textureOffset;
    private final boolean textureMirrored;
    private final float[] cubeGrow;
    private final List<CubeInfo> children = new ArrayList<>();

    private final float[][] generatedUVs = new float[6][4];

    public CubeInfo(ModelInfo model, String name, int[] dimensions, float[] rotationPoint, float[] offset, float[] rotation, int[] textureOffset, boolean textureMirrored, float[] cubeGrow) {
        this.model = model;
        this.name = name;
        this.dimensions = dimensions;
        this.rotationPoint = rotationPoint;
        this.offset = offset;
        this.rotation = new float[] {(float) (rotation[0]*Math.PI/180), (float) (rotation[1]*Math.PI/180), (float) (rotation[2]*Math.PI/180)};
        this.textureOffset = textureOffset;
        this.textureMirrored = textureMirrored;
        this.cubeGrow = cubeGrow;
        this.generateUVS();
    }

    public String getName() {
        return this.name;
    }

    public int[] getDimensions() {
        return this.dimensions;
    }

    public float[] getRotationPoint() {
        return this.rotationPoint;
    }

    public float[] getOffset() {
        return this.offset;
    }

    public float[] getRotation() {
        return this.rotation;
    }

    public int[] getTextureOffset() {
        return this.textureOffset;
    }

    public boolean isTextureMirrored() {
        return this.textureMirrored;
    }

    public float[] getCubeGrow() {
        return this.cubeGrow;
    }

    public List<CubeInfo> getChildren() {
        return this.children;
    }

    public ModelInfo getModel() {
        return this.model;
    }

    public float[][] getGeneratedUVs() {
        return generatedUVs;
    }

    @Override
    public String toString() {
        return "CubeInfo{" +
            "name='" + name + '\'' +
            ", dimensions=" + Arrays.toString(dimensions) +
            ", rotationPoint=" + Arrays.toString(rotationPoint) +
            ", offset=" + Arrays.toString(offset) +
            ", rotation=" + Arrays.toString(rotation) +
            ", textureOffset=" + Arrays.toString(textureOffset) +
            ", textureMirrored=" + textureMirrored +
            ", cubeGrow=" + Arrays.toString(cubeGrow) +
            '}';
    }

    /**
     * Generates the uv map for each face.
     * <pre>{@code
     * The texture format is as follows:
     *        This is the minimum XY coord defined in the cube.getTexOffset()
     *                \
     *                 \               width       width
     *                  \         <------------><----------->
     *                   \
     *               Ʌ    X       ---------------------------
     *        depth  |            |     UP     |    DOWN    |
     *               V            |            |            |
     *               Ʌ    -------------------------------------------
     *               |    |       |            |       |            |
     *               |    |       |            |       |            |
     *       height  |    |  WEST |   NORTH    |  EAST |    SOUTH   |
     *               |    |       |            |       |            |
     *               |    |       |            |       |            |
     *               V    -------------------------------------------
     *                    <-------><-----------><------><----------->
     *                      depth      width      depth      width
     * }</pre>
     */
    private void generateUVS() {
        //List is made up of entries:
        //+x,-x,+y,-y,+z,-z
        //Each entry is composed of:
        //[uFrom, vFrom, uTo, vTo]

        int w = this.dimensions[0];
        int h = this.dimensions[1];
        int d = this.dimensions[2];

        //1, 3, 4, 5, 0, 2
        this.generateFace(this.generatedUVs[0], d+w, d, d, h);      //+x
        this.generateFace(this.generatedUVs[1], 0, d, d, h);        //-x
        this.generateFace(this.generatedUVs[2], d, 0, w, d);        //+y
        this.generateFace(this.generatedUVs[3], d+w, 0, w, d); //-y
        this.generateFace(this.generatedUVs[4], d+w+d, d, w, h);    //+z
        this.generateFace(this.generatedUVs[5], d, d, w, h);             //-z
    }

    private void generateFace(float[] data, int offU, int offV, int heightU, int heightV) {
        float w = this.model.getTextureWidth();
        float h = this.model.getTextureHeight();

        int u = this.textureOffset[0];
        int v = this.textureOffset[1];

        data[0] = (u + offU) / w;
        data[1] = (v + offV) / h ;
        data[2] = (u + offU + heightU) / w;
        data[3] = (v + offV + heightV) / h;
    }
}
