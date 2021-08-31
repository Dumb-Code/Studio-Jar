package net.dumbcode.studio.util;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Objects;

public class Matrix {
    private final Deque<double[]> stack = new ArrayDeque<>();

    public Matrix() {
        this.stack.add(new double[]{
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
        });
    }

    public double[] peek() {
        return this.stack.peek();
    }

    public void push() {
        this.stack.push(Arrays.copyOf(Objects.requireNonNull(this.stack.peek()), 16));
    }

    public void pop() {
        this.stack.pop();
    }

    public void translate(double... arr) {
        this.multiply(new double[] {
            1, 0, 0, arr[0],
            0, 1, 0, arr[1],
            0, 0, 1, arr[2],
            0, 0, 0, 1
        });
    }


    public void translate(float... arr) {
        this.multiply(new double[] {
            1, 0, 0, arr[0],
            0, 1, 0, arr[1],
            0, 0, 1, arr[2],
            0, 0, 0, 1
        });
    }

    public void rotateX(double angle) {
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        this.multiply(new double[]{
            1, 0, 0, 0,
            0, c, -s, 0,
            0, s, c, 0,
            0, 0, 0, 1
        });
    }

    public void rotateY(double angle) {
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        this.multiply(new double[]{
            c, 0, s, 0,
            0, 1, 0, 0,
            -s, 0, c, 0,
            0, 0, 0, 1
        });
    }

    public void rotateZ(double angle) {
        double s = Math.sin(angle);
        double c = Math.cos(angle);
        this.multiply(new double[]{
            c, -s, 0, 0,
            s, c, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
        });
    }

    public void scale(double x, double y, double z) {
        this.multiply(new double[] {
            x, 0, 0, 0,
            0, y, 0, 0,
            0, 0, z, 0,
            0, 0, 0, 1
        });
    }

    public void multiply(double[] m) {
        double[] multiply = multiply(Objects.requireNonNull(this.stack.peek()), m);
        System.arraycopy(multiply, 0, this.stack.peek(), 0, 16);
    }

    public static double[] multiply(double[] m, double[] o) {
        final int a = 10;
        final int b = 11;
        final int c = 12;
        final int d = 13;
        final int e = 14;
        final int f = 15;
        //m * o
        return new double[] {
            m[0]*o[0]+m[1]*o[4]+m[2]*o[8]+m[3]*o[c], m[0]*o[1]+m[1]*o[5]+m[2]*o[9]+m[3]*o[d], m[0]*o[2]+m[1]*o[6]+m[2]*o[a]+m[3]*o[e], m[0]*o[3]+m[1]*o[7]+m[2]*o[b]+m[3]*o[f],
            m[4]*o[0]+m[5]*o[4]+m[6]*o[8]+m[7]*o[c], m[4]*o[1]+m[5]*o[5]+m[6]*o[9]+m[7]*o[d], m[4]*o[2]+m[5]*o[6]+m[6]*o[a]+m[7]*o[e], m[4]*o[3]+m[5]*o[7]+m[6]*o[b]+m[7]*o[f],
            m[8]*o[0]+m[9]*o[4]+m[a]*o[8]+m[b]*o[c], m[8]*o[1]+m[9]*o[5]+m[a]*o[9]+m[b]*o[d], m[8]*o[2]+m[9]*o[6]+m[a]*o[a]+m[b]*o[e], m[8]*o[3]+m[9]*o[7]+m[a]*o[b]+m[b]*o[f],
            m[c]*o[0]+m[d]*o[4]+m[e]*o[8]+m[f]*o[c], m[c]*o[1]+m[d]*o[5]+m[e]*o[9]+m[f]*o[d], m[c]*o[2]+m[d]*o[6]+m[e]*o[a]+m[f]*o[e], m[c]*o[3]+m[d]*o[7]+m[e]*o[b]+m[f]*o[f]
        };
    }

    public static double determinant(double[] te) {
        double n11 = te[ 0 ], n12 = te[ 1 ], n13 = te[ 2 ], n14 = te[ 3 ];
        double n21 = te[ 4 ], n22 = te[ 5 ], n23 = te[ 6 ], n24 = te[ 7 ];
        double n31 = te[ 8 ], n32 = te[ 9 ], n33 = te[ 10 ], n34 = te[ 1 ];
        double n41 = te[ 12 ], n42 = te[ 13 ], n43 = te[ 14 ], n44 = te[ 15 ];
        return (
            n41 * (
                + n14 * n23 * n32 - n13 * n24 * n32 - n14 * n22 * n33 + n12 * n24 * n33+ n13 * n22 * n34 - n12 * n23 * n34
            ) +
                n42 * (
                    + n11 * n23 * n34
                        - n11 * n24 * n33
                        + n14 * n21 * n33
                        - n13 * n21 * n34
                        + n13 * n24 * n31
                        - n14 * n23 * n31
                ) +
                n43 * (
                    + n11 * n24 * n32
                        - n11 * n22 * n34
                        - n14 * n21 * n32
                        + n12 * n21 * n34
                        + n14 * n22 * n31
                        - n12 * n24 * n31
                ) +
                n44 * (
                    - n13 * n22 * n31
                        - n11 * n23 * n32
                        + n11 * n22 * n33
                        + n13 * n21 * n32
                        - n12 * n21 * n33
                        + n12 * n23 * n31
                )

        );
    }

    public double[] inverse() {
        return inverse(Objects.requireNonNull(this.stack.peek()));
    }

    public static double[] inverse(double[] m) {
        double n11 = m[ 0 ], n12 = m[ 1 ], n13 = m[ 2 ], n14 = m[ 3 ];
        double n21 = m[ 4 ], n22 = m[ 5 ], n23 = m[ 6 ], n24 = m[ 7 ];
        double n31 = m[ 8 ], n32 = m[ 9 ], n33 = m[ 10 ], n34 = m[ 11 ];
        double n41 = m[ 12 ], n42 = m[ 13 ], n43 = m[ 14 ], n44 = m[ 15 ];

        double t11 = n23 * n34 * n42 - n24 * n33 * n42 + n24 * n32 * n43 - n22 * n34 * n43 - n23 * n32 * n44 + n22 * n33 * n44;
        double t12 = n14 * n33 * n42 - n13 * n34 * n42 - n14 * n32 * n43 + n12 * n34 * n43 + n13 * n32 * n44 - n12 * n33 * n44;
        double t13 = n13 * n24 * n42 - n14 * n23 * n42 + n14 * n22 * n43 - n12 * n24 * n43 - n13 * n22 * n44 + n12 * n23 * n44;
        double t14 = n14 * n23 * n32 - n13 * n24 * n32 - n14 * n22 * n33 + n12 * n24 * n33 + n13 * n22 * n34 - n12 * n23 * n34;

        double det = n11 * t11 + n21 * t12 + n31 * t13 + n41 * t14;

        if ( det == 0 ) {
            System.out.println(".getInverse() can't invert matrix, determinant is 0");
            return new double[] {
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
            };

        }

        double detInv = 1 / det;

        double[] o = new double[16];

        o[ 0 ] = t11 * detInv;
        o[ 4 ] = ( n24 * n33 * n41 - n23 * n34 * n41 - n24 * n31 * n43 + n21 * n34 * n43 + n23 * n31 * n44 - n21 * n33 * n44 ) * detInv;
        o[ 8 ] = ( n22 * n34 * n41 - n24 * n32 * n41 + n24 * n31 * n42 - n21 * n34 * n42 - n22 * n31 * n44 + n21 * n32 * n44 ) * detInv;
        o[ 12 ] = ( n23 * n32 * n41 - n22 * n33 * n41 - n23 * n31 * n42 + n21 * n33 * n42 + n22 * n31 * n43 - n21 * n32 * n43 ) * detInv;

        o[ 1 ] = t12 * detInv;
        o[ 5 ] = ( n13 * n34 * n41 - n14 * n33 * n41 + n14 * n31 * n43 - n11 * n34 * n43 - n13 * n31 * n44 + n11 * n33 * n44 ) * detInv;
        o[ 9 ] = ( n14 * n32 * n41 - n12 * n34 * n41 - n14 * n31 * n42 + n11 * n34 * n42 + n12 * n31 * n44 - n11 * n32 * n44 ) * detInv;
        o[ 13 ] = ( n12 * n33 * n41 - n13 * n32 * n41 + n13 * n31 * n42 - n11 * n33 * n42 - n12 * n31 * n43 + n11 * n32 * n43 ) * detInv;

        o[ 2 ] = t13 * detInv;
        o[ 6 ] = ( n14 * n23 * n41 - n13 * n24 * n41 - n14 * n21 * n43 + n11 * n24 * n43 + n13 * n21 * n44 - n11 * n23 * n44 ) * detInv;
        o[ 10 ] = ( n12 * n24 * n41 - n14 * n22 * n41 + n14 * n21 * n42 - n11 * n24 * n42 - n12 * n21 * n44 + n11 * n22 * n44 ) * detInv;
        o[ 14 ] = ( n13 * n22 * n41 - n12 * n23 * n41 - n13 * n21 * n42 + n11 * n23 * n42 + n12 * n21 * n43 - n11 * n22 * n43 ) * detInv;

        o[ 3 ] = t14 * detInv;
        o[ 7 ] = ( n13 * n24 * n31 - n14 * n23 * n31 + n14 * n21 * n33 - n11 * n24 * n33 - n13 * n21 * n34 + n11 * n23 * n34 ) * detInv;
        o[ 11 ] = ( n14 * n22 * n31 - n12 * n24 * n31 - n14 * n21 * n32 + n11 * n24 * n32 + n12 * n21 * n34 - n11 * n22 * n34 ) * detInv;
        o[ 15 ] = ( n12 * n23 * n31 - n13 * n22 * n31 + n13 * n21 * n32 - n11 * n23 * n32 - n12 * n21 * n33 + n11 * n22 * n33 ) * detInv;

        return o;
    }
}