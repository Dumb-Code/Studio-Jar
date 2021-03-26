package net.dumbcode.studio;

import net.dumbcode.studio.model.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class StudioJar {

	public static void main(String[] args) throws IOException {
		File file = new File("C:\\Users\\wynpr\\Downloads\\New Model (3) (2) (1).dcm");
		ModelInfo info = ModelLoader.loadModel(new FileInputStream(file), RotationOrder.ZYX, ModelMirror.XY);
		ModelWriter.writeModel(info, new FileOutputStream("C:\\Users\\wynpr\\Downloads\\New Model_out.dcm"));

	}
}
