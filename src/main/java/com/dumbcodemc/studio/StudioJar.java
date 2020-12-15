package com.dumbcodemc.studio;

import com.dumbcodemc.studio.animation.info.AnimationInfo;
import com.dumbcodemc.studio.animation.info.AnimationLoader;
import com.dumbcodemc.studio.model.ModelInfo;
import com.dumbcodemc.studio.model.ModelLoader;

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
