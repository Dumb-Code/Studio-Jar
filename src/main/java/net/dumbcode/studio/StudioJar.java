package net.dumbcode.studio;

import net.dumbcode.studio.animation.info.AnimationInfo;
import net.dumbcode.studio.animation.info.AnimationLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class StudioJar {

	public static void main(String[] args) throws IOException {
//		ModelInfo modelInfo = ModelLoader.loadModel(new FileInputStream(new File("C:\\Users\\wynpr\\Documents\\GitHub\\TVE-ExampleClient\\src\\main\\resources\\assets\\testclient\\models\\Gerald.dcm")));
		AnimationInfo animationInfo = AnimationLoader.loadAnimation(new FileInputStream(new File("C:\\Users\\wynpr\\Documents\\GitHub\\TVE-ExampleClient\\src\\main\\resources\\assets\\testclient\\animations\\wave.dca")));
		System.out.println(animationInfo);
	}
}
