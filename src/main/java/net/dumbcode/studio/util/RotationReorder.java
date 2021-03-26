package net.dumbcode.studio.util;

import net.dumbcode.studio.model.RotationOrder;

public class RotationReorder {

    private static final double ONE_ARC_SIN = Math.asin(1);

    public static float[] reorder(float[] arr, RotationOrder from, RotationOrder to) {
        if(from == to) {
            return arr;
        }

        double[] quat = createQuaternion(arr, from);
        double[] matrix = makeRotationMatrix(quat);
        double[] angles = createEulerFromMatrix(matrix, to);

        arr[0] = (float) angles[0];
        arr[1] = (float) angles[1];
        arr[2] = (float) angles[2];

        return arr;
    }

    public static double[] createEulerFromMatrix(double[] matrix, RotationOrder order) {
        double m11 = matrix[0], m12 = matrix[3], m13 = matrix[6];
        double m21 = matrix[1], m22 = matrix[4], m23 = matrix[7];
        double m31 = matrix[2], m32 = matrix[5], m33 = matrix[8];

        double x,y,z;

        switch (order) {
            case XYZ:
                y = clampArcSin(m13);
                if (Math.abs(m13) < 0.9999999) {
                    x = Math.atan2( - m23, m33 );
                    z = Math.atan2( - m12, m11 );
                } else {
                    x = Math.atan2( m32, m22 );
                    z = 0;
                }
                break;
            case YXZ:
                x = -clampArcSin(m23);
                if (Math.abs(m23) < 0.9999999) {
                    y = Math.atan2(m13, m33);
                    z = Math.atan2(m21, m22);
                } else {
                    y = Math.atan2(-m31, m11);
                    z = 0;
                }
                break;
            case ZXY:
                x = clampArcSin(m32);
                if (Math.abs(m32) < 0.9999999) {
                    y = Math.atan2(-m31, m33);
                    z = Math.atan2(-m12, m22);
                } else {
                    y = 0;
                    z = Math.atan2(m21, m11);
                }
                break;
            case ZYX:
                y = -clampArcSin(m31);
                if (Math.abs(m31) < 0.9999999) {
                    x = Math.atan2(m32, m33);
                    z = Math.atan2(m21, m11);
                } else {
                    x = 0;
                    z = Math.atan2(-m12, m22 );
                }
                break;
            case YZX:
                z = clampArcSin(m21);
                if (Math.abs(m21) < 0.9999999) {
                    x = Math.atan2(-m23, m22);
                    y = Math.atan2(-m31, m11);
                } else {
                    x = 0;
                    y = Math.atan2(m13, m33);
                }
                break;
            case XZY:
                z = -clampArcSin(m12);
                if (Math.abs(m12) < 0.9999999) {
                    x = Math.atan2(m32, m22);
                    y = Math.atan2(m13, m11);
                } else {
                    x = Math.atan2(-m23, m33);
                    y = 0;
                }
                break;
            default:
                throw new IllegalArgumentException("Don't know order " + order);
        }

        return new double[] {x, y, z};
    }

    private static double clampArcSin(double num) {
        if(num <= -1) {
            return -ONE_ARC_SIN;
        }
        if(num >= 1) {
            return ONE_ARC_SIN;
        }
        return Math.asin(num);
    }

    private static double[] makeRotationMatrix(double[] quat) {
        double[] ret = new double[9];

        double x = quat[0], y = quat[1], z = quat[2], w = quat[3];
        double x2 = x + x,	y2 = y + y, z2 = z + z;
        double xx = x * x2, xy = x * y2, xz = x * z2;
        double yy = y * y2, yz = y * z2, zz = z * z2;
        double wx = w * x2, wy = w * y2, wz = w * z2;

        ret[0] = 1 - (yy + zz);
        ret[1] = xy + wz;
        ret[2] = xz - wy;

        ret[3] = xy - wz;
        ret[4] = 1 - (xx + zz);
        ret[5] = yz + wx;

        ret[6] = xz + wy;
        ret[7] = yz - wx;
        ret[8] = 1 - (xx + yy);

        return ret;
    }

    private static double[] createQuaternion(float[] arr, RotationOrder order) {
        float xi = arr[0];
        float yi = arr[1];
        float zi = arr[2];

        double c1 = Math.cos( xi / 2 );
        double c2 = Math.cos( yi / 2 );
        double c3 = Math.cos( zi / 2 );

        double s1 = Math.sin( xi / 2 );
        double s2 = Math.sin( yi / 2 );
        double s3 = Math.sin( zi / 2 );

        double xL = s1 * c2 * c3;
        double xR = c1 * s2 * s3;

        double yL = c1 * s2 * c3;
        double yR = s1 * c2 * s3;

        double zL = c1 * c2 * s3;
        double zR = s1 * s2 * c3;

        double wL = c1 * c2 * c3;
        double wR = s1 * s2 * s3;


        double x,y,z,w;
        switch (order) {
            case XYZ:
                x = xL + xR;
                y = yL - yR;
                z = zL + zR;
                w = wL - wR;
                break;
            case YXZ:
                x = xL + xR;
                y = yL - yR;
                z = zL - zR;
                w = wL + wR;
                break;
            case ZXY:
                x = xL - xR;
                y = yL + yR;
                z = zL + zR;
                w = wL - wR;
                break;
            case ZYX:
                x = xL - xR;
                y = yL + yR;
                z = zL - zR;
                w = wL + wR;
                break;
            case YZX:
                x = xL + xR;
                y = yL + yR;
                z = zL - zR;
                w = wL - wR;
                break;
            case XZY:
                x = xL - xR;
                y = yL - yR;
                z = zL + zR;
                w = wL + wR;
                break;
            default:
                throw new IllegalArgumentException("Don't know order " + order);
        }

        return new double[] { x, y, z, w };
    }
}