package com.dumbcodemc.studio;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ModelLoader {

	private static ArrayList<ModelCube> readCubes(DataInputStream data, ModelInfo model) throws IOException {
		ArrayList<ModelCube> cubes = new ArrayList<>();
		int amount = (int) data.readFloat();
		for (int i = 0; i < amount; i++) {
			cubes.add(new ModelCube(
					data.readUTF(),
					new int[]{ (int)data.readFloat(), (int)data.readFloat(), (int)data.readFloat()}, //Dimensions
					new float[]{ data.readFloat(), data.readFloat(), data.readFloat()}, //Rotation Point
					new float[]{ data.readFloat(), data.readFloat(), data.readFloat()}, //Offset
					new float[]{ data.readFloat(), data.readFloat(), data.readFloat()}, //Rotation
					new int[]{ (int)data.readFloat(), (int)data.readFloat()}, //Texture Offset
					data.readBoolean(), //Texture Mirrored
					new float[]{ data.readFloat(), data.readFloat(), data.readFloat()}, //Cube Grow
					readCubes(data, model), //Children
					model)); //Create the array for this child's location
		}
		return cubes;
	}

	public static ModelInfo load(DataInputStream data) {
		ModelInfo model = new ModelInfo();
		try {
			model.version = data.readFloat();
			model.author = data.readUTF();
			model.textureWidth = (int) data.readFloat();
			model.textureHeight = (int) data.readFloat();
			model.children = readCubes(data, model);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return model;
	}
}
