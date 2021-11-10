package net.dumbcode.studio.animation.instance;

import net.dumbcode.studio.animation.info.AnimationInfo;
import net.dumbcode.studio.animation.info.KeyframeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnimationCapture extends AnimationConsumer {

    public static final AnimationCapture CAPTURE = new AnimationCapture();

    private Map<String, float[]> position;
    private Map<String, float[]> rotation;
    private Map<String, float[]> cubeGrow;

    private AnimationCapture() {}

    @Override
    protected void addPosition(String name, float x, float y, float z) {
        float[] values = this.position.computeIfAbsent(name, k -> new float[3]);
        values[0] += x;
        values[1] += y;
        values[2] += z;
    }

    @Override
    protected void addRotation(String name, float x, float y, float z) {
        float[] values = this.rotation.computeIfAbsent(name, k -> new float[3]);
        values[0] += x;
        values[1] += y;
        values[2] += z;
    }

    @Override
    protected void addCubeGrow(String name, float x, float y, float z) {
        float[] values = this.cubeGrow.computeIfAbsent(name, k -> new float[3]);
        values[0] += x;
        values[1] += y;
        values[2] += z;
    }

    public void captureAnimation(List<KeyframeInfo> keyframes, float time,
                                 Map<String, float[]> position,
                                 Map<String, float[]> rotation,
                                 Map<String, float[]> cubeGrow) {
        this.infos = new ArrayList<>(keyframes);

        this.position = position;
        this.position.clear();

        this.rotation = rotation;
        this.rotation.clear();

        this.cubeGrow = cubeGrow;
        this.cubeGrow.clear();

        this.animateAtTime(time);
    }
}
