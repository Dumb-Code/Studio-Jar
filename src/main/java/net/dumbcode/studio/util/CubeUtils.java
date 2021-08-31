package net.dumbcode.studio.util;

import net.dumbcode.studio.model.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class CubeUtils {
    public static double[] getWorldPosition(EssentiallyACube cube, double x, double y, double z) {
        Matrix matrix = new Matrix();
        transformCube(cube, matrix);

        float[] offset = cube.getOffset();
        float[] cubeGrow = cube.getCubeGrow();
        int[] dimensions = cube.getDimensions();
        matrix.translate(
            (offset[0] - cubeGrow[0] + (dimensions[0] + cubeGrow[0]*2) * x),
            (offset[1] - cubeGrow[1] + (dimensions[1] + cubeGrow[1]*2) * y),
            (offset[2] - cubeGrow[2] + (dimensions[2] + cubeGrow[2]*2) * z)
        );
        double[] doubles = matrix.peek();
        return new double[] { doubles[3], doubles[7], doubles[11] };
    }

    private static void transformCube(EssentiallyACube cube, Matrix matrix) {
        if(cube.getParent() != null) {
            transformCube(cube.getParent(), matrix);
        }
        matrix.translate(cube.getRotationPoint());

        rotate(cube.getRotationOrder().getFirst(), matrix, cube.getRotation());
        rotate(cube.getRotationOrder().getSecond(), matrix, cube.getRotation());
        rotate(cube.getRotationOrder().getThird(), matrix, cube.getRotation());
    }

    private static void rotate(int part, Matrix matrix, float[] rotation) {
        switch (part) {
            case 0:
                matrix.rotateX(rotation[0]);
                return;
            case 1:
                matrix.rotateY(rotation[1]);
                return;
            case 2:
                matrix.rotateZ(rotation[2]);
        }
    }
}
