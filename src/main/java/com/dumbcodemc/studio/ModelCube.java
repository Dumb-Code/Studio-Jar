package com.dumbcodemc.studio;

import java.util.ArrayList;

public class ModelCube {

	public String name;
	public int[] dimensions;
	public float[] rotationPoint;
	public float[] offset;
	public float[] rotation;
	public int[] textureOffset;
	public boolean textureMirrored;
	public float[] scale;
	public ArrayList<ModelCube> children;
	public ModelInfo model;
	public float[][] uvs;

	public ModelCube(String name, int[] dimensions, float[] rotationPoint, float[] offset, float[] rotation,
					 int[] textureOffset, boolean textureMirrored, float[] scale, ArrayList<ModelCube> children,
					 ModelInfo model) {
		this.name = name;
		this.dimensions = dimensions;
		this.rotationPoint = rotationPoint;
		this.offset = offset;
		this.rotation = rotation;
		this.textureOffset = textureOffset;
		this.textureMirrored = textureMirrored;
		this.scale = scale;
		this.children = children;
		this.model = model;
		this.uvs = setUVs();
	}

	private float[][] setUVs() {

		float[][] pixelUvs = new float[6][4];
		//South
		pixelUvs[0][0] = textureOffset[0] + (2 * dimensions[2]) + dimensions[0];
		pixelUvs[0][1] = textureOffset[1] + dimensions[2];
		pixelUvs[0][2] = textureOffset[0] + (2 * dimensions[2]) + (2 * dimensions[0]);
		pixelUvs[0][3] = textureOffset[1] + dimensions[2] + dimensions[1];
		//East
		pixelUvs[1][0] = textureOffset[0] + dimensions[2] + dimensions[0];
		pixelUvs[1][1] = textureOffset[1] + dimensions[2];
		pixelUvs[1][2] = textureOffset[0] + (2 * dimensions[2]) + dimensions[0];
		pixelUvs[1][3] = textureOffset[1] + dimensions[2] + dimensions[1];
		//North
		pixelUvs[2][0] = textureOffset[0] + dimensions[2];
		pixelUvs[2][1] = textureOffset[1] + dimensions[2];
		pixelUvs[2][2] = textureOffset[0] + dimensions[2] + dimensions[0];
		pixelUvs[2][3] = textureOffset[1] + dimensions[2] + dimensions[1];
		//West
		pixelUvs[3][0] = textureOffset[0];
		pixelUvs[3][1] = textureOffset[1] + dimensions[2];
		pixelUvs[3][2] = textureOffset[0] + dimensions[2];
		pixelUvs[3][3] = textureOffset[1] + dimensions[2] + dimensions[1];
		//Top
		pixelUvs[4][0] = textureOffset[0] + dimensions[2];
		pixelUvs[4][1] = textureOffset[1];
		pixelUvs[4][2] = textureOffset[0] + dimensions[2] + dimensions[0];
		pixelUvs[4][3] = textureOffset[1] + dimensions[2];
		//Bottom
		pixelUvs[5][0] = textureOffset[0] + dimensions[2] + dimensions[0];
		pixelUvs[5][1] = textureOffset[1];
		pixelUvs[5][2] = textureOffset[0] + (2 * dimensions[2]) + dimensions[0];
		pixelUvs[5][3] = textureOffset[1] + dimensions[2];

		//clamp the values from 0 to 1
		for (int i = 0; i < pixelUvs.length; i++) {
			pixelUvs[i][0] /= model.textureWidth;
			pixelUvs[i][1] /= model.textureHeight;
			pixelUvs[i][2] /= model.textureWidth;
			pixelUvs[i][3] /= model.textureHeight;
		}
		//Account for rounding errors in the clamping
		for (int i = 0; i < pixelUvs.length; i++) {
			pixelUvs[i][0] += 0.01 / model.textureWidth;
			pixelUvs[i][1] += 0.01 / model.textureHeight;
			pixelUvs[i][2] -= 0.01 / model.textureWidth;
			pixelUvs[i][3] -= 0.01 / model.textureHeight;
		}

		return pixelUvs;
	}
}
