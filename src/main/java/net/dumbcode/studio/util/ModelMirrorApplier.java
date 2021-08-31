package net.dumbcode.studio.util;

import net.dumbcode.studio.model.CubeInfo;
import net.dumbcode.studio.model.ModelLoader;
import net.dumbcode.studio.model.ModelMirror;
import net.dumbcode.studio.model.RotationOrder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;

//Good luck to anyone who looks at this class. I can tell you it certainly works, but is very ugly
//to look at.
//
//Essentially, just mirrors a model on it's axis.
public class ModelMirrorApplier {
    
    public static void mirrorCubes(List<CubeInfo> roots, ModelMirror to) {
        Matrix matrix = new Matrix();
        float[] origin = to.getOrigin();
        for (float[] normal : to.getPlaneNormals()) {
            UnaryOperator<double[]> mirrorPoint = vec -> {
                //Definition of a plane at point (x0, y0, z0) (var: worldPos) with normal (A, B, C) (var: normal):
                //A(x − x0) + B(y − y0) + C(z − z0) = 0
                //
                //I want to find the projection point (x,y,z) (var: vec) onto the plane. This would be defined as (x+At, y+Bt, z+Ct), where t is a random variable
                //Putting that back into the plane equation:
                //      A((x+At)-x0) + B((y+Bt)-y0) + C((z+Ct)-z0) = 0
                //  =>  A(x+At-x0) + B(y+Bt-y0) + C(z+Ct-z0) = 0
                //  =>  Ax+AAt-Ax0 + By+BBt-By0 + Cz+CCT-Cz0 = 0
                //  =>  AAt + BBt + CCt = Ax0-Ax + By0-By + Cz0-Cz
                //  =>  t = (Ax0-Ax + By0-By + Cz0-Cz) / (AA+BB+CC)
                //
                //Once t is found, I can put it back into (x+At, y+Bt, z+Ct) to give me the projection point on the plane.
                //With the projection point on the plane, I can find the difference between that and the starting point, and move the point by that distance again.
                double t = (
                    normal[0]*(origin[0]-vec[0]) +
                    normal[1]*(origin[1]-vec[1]) +
                    normal[2]*(origin[2]-vec[2])
                ) / (normal[0]*normal[0] + normal[1]*normal[1] + normal[2]*normal[2]);
                return new double[] {
                    vec[0]+2*t*normal[0],
                    vec[1]+2*t*normal[1],
                    vec[2]+2*t*normal[2]
                };
            };

            CubeData[] rootsData = new CubeData[roots.size()];
            for (int i = 0; i < roots.size(); i++) {
                rootsData[i] = traverseCube(roots.get(i), matrix, mirrorPoint);
            }

            for (CubeData datum : rootsData) {
                applyCubeData(datum, matrix);
            }

        }
    }

    private static void applyCubeData(CubeData data, Matrix matrix) {
        CubeInfo cube = data.info;
        RotationOrder order = cube.getRotationOrder();

        double[] peek = matrix.peek();
        double[] inverse = matrix.inverse();
        double[] local = Matrix.multiply(inverse, data.newMatrix);


        matrix.push();

        cube.getRotationPoint()[0] = (float) local[3];
        cube.getRotationPoint()[1] = (float) local[7];
        cube.getRotationPoint()[2] = (float) local[11];

        double[] angles = RotationReorder.createEulerFromMatrix(getRotationMatrixColumns(local), cube.getRotationOrder());
        cube.getRotation()[0] = (float) angles[0];
        cube.getRotation()[1] = (float) angles[1];
        cube.getRotation()[2] = (float) angles[2];
        cube.generateAllDefaultRotations();

        matrix.translate(cube.getRotationPoint());
        applyRotation(order.getFirst(), cube, matrix);
        applyRotation(order.getSecond(), cube, matrix);
        applyRotation(order.getThird(), cube, matrix);

        matrix.push();
        matrix.translate(
            cube.getOffset()[0]-cube.getCubeGrow()[0],
            cube.getOffset()[1]-cube.getCubeGrow()[1],
            cube.getOffset()[2]-cube.getCubeGrow()[2]
        );

        double[] m = Matrix.inverse(data.rotationMatrix);
        Function<Boolean, double[]> cubeWorldCorner = farEnd -> {
            double[] pos = farEnd ? new double[] {
                cube.getDimensions()[0] + 2*cube.getCubeGrow()[0],
                cube.getDimensions()[1] + 2*cube.getCubeGrow()[1],
                cube.getDimensions()[2] + 2*cube.getCubeGrow()[2],
            } : new double[3];
            matrix.push();
            matrix.translate(pos);
            double[] doubles = Objects.requireNonNull(matrix.peek());
            matrix.pop();
            return new double[] { doubles[3], doubles[7], doubles[11], };
        };

        double[] c0 = cubeWorldCorner.apply(false);
        double[] toMove0 = new double[] { c0[0]-data.base[0], c0[1]-data.base[1], c0[2]-data.base[2] };

        double[] c1 = cubeWorldCorner.apply(true);
        double[] toMove1 = new double[] { c1[0]-data.oldCorner[0], c1[1]-data.oldCorner[1], c1[2]-data.oldCorner[2] };

        double[] t = new double[] { toMove0[0]+toMove1[0], toMove0[1]+toMove1[1], toMove0[2]+toMove1[2] };

        double[] appliedToMove = new double[] {
            (t[0]*m[0] + t[1]*m[1] + t[2]*m[2])/2,
            (t[0]*m[4] + t[1]*m[5] + t[2]*m[6])/2,
            (t[0]*m[8] + t[1]*m[9] + t[2]*m[10])/2,
        };
        cube.getOffset()[0] += appliedToMove[0];
        cube.getOffset()[1] += appliedToMove[1];
        cube.getOffset()[2] += appliedToMove[2];

        matrix.pop();

        for (CubeData child : data.children) {
            applyCubeData(child, matrix);
        }

        matrix.pop();
    }

    private static CubeData traverseCube(CubeInfo cube, Matrix matrix, UnaryOperator<double[]> mirrorPoint) {
        matrix.push();

        matrix.translate(cube.getRotationPoint());

        RotationOrder order = cube.getRotationOrder();
        applyRotation(order.getFirst(), cube, matrix);
        applyRotation(order.getSecond(), cube, matrix);
        applyRotation(order.getThird(), cube, matrix);

        double[] mat = Objects.requireNonNull(matrix.peek());
        double[] position = mirrorPoint.apply(new double[] { mat[3], mat[7], mat[11] });

        matrix.push();
        matrix.translate(
            cube.getOffset()[0]-cube.getCubeGrow()[0],
            cube.getOffset()[1]-cube.getCubeGrow()[1],
            cube.getOffset()[2]-cube.getCubeGrow()[2]
        );
        Function<Boolean, double[]> cubeWorldCorner = farEnd -> {
            double[] pos = farEnd ? new double[] {
                cube.getDimensions()[0] + cube.getCubeGrow()[0]*2,
                cube.getDimensions()[1] + cube.getCubeGrow()[1]*2,
                cube.getDimensions()[2] + cube.getCubeGrow()[2]*2,
            } : new double[3];
            matrix.push();
            matrix.translate(pos);
            double[] doubles = Objects.requireNonNull(matrix.peek());
            matrix.pop();
            return new double[] { doubles[3], doubles[7], doubles[11], };
        };
        Function<Integer, double[]> cubeWorldAxis = axis -> {
            double[] pos = new double[] { axis==0?1:0, axis==1?1:0, axis==2?1:0 };
            matrix.push();
            matrix.translate(pos);
            double[] doubles = Objects.requireNonNull(matrix.peek());
            matrix.pop();
            return new double[] { doubles[3], doubles[7], doubles[11], };
        };

        double[] base = mirrorPoint.apply(cubeWorldCorner.apply(false));
        double[] corner = mirrorPoint.apply(cubeWorldCorner.apply(true));

        double[] xAxis = subAndNormalize(mirrorPoint.apply(cubeWorldAxis.apply(0)), base);
        double[] yAxis = subAndNormalize(mirrorPoint.apply(cubeWorldAxis.apply(1)), base);
        double[] zAxis = subAndNormalize(mirrorPoint.apply(cubeWorldAxis.apply(2)), base);

        double[] positionMatrix = new double[] {
            1, 0, 0, position[0],
            0, 1, 0, position[1],
            0, 0, 1, position[2],
            0, 0, 0, 1,
        };
        double[] rotationMatrix = new double[] {
            xAxis[0], yAxis[0], zAxis[0], 0,
            xAxis[1], yAxis[1], zAxis[1], 0,
            xAxis[2], yAxis[2], zAxis[2], 0,
            0, 0, 0, 1
        };
        double[] newMatrix = Matrix.multiply(positionMatrix, rotationMatrix);
        matrix.pop();

        CubeData[] children = new CubeData[cube.getChildren().toArray().length];
        for (int i = 0; i < children.length; i++) {
            children[i] = traverseCube(cube.getChildren().get(i), matrix, mirrorPoint);

        }
        matrix.pop();

        return new CubeData(cube, newMatrix, rotationMatrix, base, corner, children);
    }

    private static double[] subAndNormalize(double[] arr, double[] sub) {
        double[] out = new double[] {
            arr[0] - sub[0],
            arr[1] - sub[1],
            arr[2] - sub[2],
        };
        double len = Math.sqrt(out[0]*out[0] + out[1]*out[1] + out[2]*out[2]);
        out[0] /= len;
        out[1] /= len;
        out[2] /= len;
        return out;
    }

    private static double[] getRotationMatrixColumns(double[] mat) {
        double invSX = 1F / Math.sqrt(mat[0]*mat[0] + mat[4]*mat[4] + mat[8]*mat[8]);
        double invSY = 1F / Math.sqrt(mat[1]*mat[1] + mat[5]*mat[5] + mat[9]*mat[9]);
        double invSZ = 1F / Math.sqrt(mat[2]*mat[2] + mat[6]*mat[6] + mat[10]*mat[10]);
        double determinant = Matrix.determinant(mat);
        if(determinant < 0) {
            invSX *= -1;
        }
        double[] copy = new double[] {
            mat[0], mat[4], mat[8],
            mat[1], mat[5], mat[9],
            mat[2], mat[6], mat[10]
        };
        copy[0] *= invSX; copy[1] *= invSX; copy[2] *= invSX;
        copy[3] *= invSY; copy[4] *= invSY; copy[5] *= invSY;
        copy[6] *= invSZ; copy[7] *= invSZ; copy[8] *= invSZ;
        return copy;
    }

    private static void applyRotation(int order, CubeInfo info, Matrix matrix) {
        switch (order) {
            default: case 0:
                matrix.rotateX(info.getRotation()[0]);
                break;
            case 1:
                matrix.rotateY(info.getRotation()[1]);
                break;
            case 2:
                matrix.rotateZ(info.getRotation()[2]);
                break;
        }
    }


    private static class CubeData {
        private final CubeInfo info;
        private final double[] newMatrix;
        private final double[] rotationMatrix;
        private final double[] base;
        private final double[] oldCorner;
        private final CubeData[] children;

        private CubeData(CubeInfo info, double[] newMatrix, double[] rotationMatrix, double[] base, double[] oldCorner, CubeData[] children) {
            this.info = info;
            this.newMatrix = newMatrix;
            this.rotationMatrix = rotationMatrix;
            this.base = base;
            this.oldCorner = oldCorner;
            this.children = children;
        }
    }
}
