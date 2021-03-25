package net.dumbcode.studio.model;

public enum RotationOrder {
    XYZ(0, 1, 2),
    YXZ(1, 0, 2),
    ZXY(2, 0, 1),
    ZYX(2, 1, 0),
    YZX(1, 2, 0),
    XZY(0, 2, 1);

    private final int first;
    private final int second;
    private final int third;

    RotationOrder(int first, int second, int third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public int getFirst() {
        return first;
    }

    public int getSecond() {
        return second;
    }

    public int getThird() {
        return third;
    }
}
