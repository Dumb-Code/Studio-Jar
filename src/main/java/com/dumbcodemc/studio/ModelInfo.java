package com.dumbcodemc.studio;

import java.util.ArrayList;
import java.util.List;

public class ModelInfo {

	protected float version;
	protected String author;
	protected int textureWidth;
	protected int textureHeight;
	protected ArrayList<ModelCube> children;

	public List<ModelCube> getChildren() {
		return children;
	}

	private String printCubes(ModelCube cube) {
		StringBuilder stringBuilder = new StringBuilder();
		for (ModelCube cubeChild : cube.children) {
			stringBuilder.append("\t");
			stringBuilder.append(cubeChild.toString());
			stringBuilder.append("\n\t").append(printCubes(cubeChild));
			stringBuilder.append("\n");
		}
		return stringBuilder.toString();
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		for (ModelCube cube : children) {
			stringBuilder.append("\n").append(cube.toString());
			stringBuilder.append(printCubes(cube));
		}
		return "DCMModel{" +
				"version=" + version +
				", author='" + author + '\'' +
				", textureWidth=" + textureWidth +
				", textureHeight=" + textureHeight +
				", children=" + stringBuilder.toString() +
				'}';
	}
}
