package net.dumbcode.studio.animation.instance;

import net.dumbcode.studio.animation.info.AnimationInfo;
import net.dumbcode.studio.animation.info.KeyframeInfo;
import net.dumbcode.studio.model.RotationOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AnimationConsumer {
    protected final List<KeyframeInfo> infos;

    public AnimationConsumer() {
        this.infos = new ArrayList<>();
    }

    public AnimationConsumer(List<KeyframeInfo> infos) {
        this.infos = infos;
    }

    protected void animateAtTime(float time) {
        for (KeyframeInfo keyframe : this.getInfo()) {
            this.animateKeyframe(keyframe, time);
        }
    }

    protected abstract void addPosition(String name, float x, float y, float z);
    protected abstract void addRotation(String name, float x, float y, float z);
    protected abstract void addCubeGrow(String name, float x, float y, float z);

    protected List<KeyframeInfo> getInfo() {
        return this.infos;
    }

    private void animateKeyframe(KeyframeInfo kf, float animTime) {
        float time = this.getProgressionValue(kf.getProgressionPoints(),(animTime - kf.getStartTime()) / kf.getDuration());

        float[] values;
        for (Map.Entry<String, float[]> entry : kf.getRotationMap().entrySet()) {
            values = entry.getValue();
            this.addRotation(entry.getKey(), values[0]*time, values[1]*time, values[2]*time);
        }
        for (Map.Entry<String, float[]> entry : kf.getPositionMap().entrySet()) {
            values = entry.getValue();
            this.addPosition(entry.getKey(), values[0]*time, values[1]*time, values[2]*time);
        }
        for (Map.Entry<String, float[]> entry : kf.getCubeGrowMap().entrySet()) {
            values = entry.getValue();
            this.addCubeGrow(entry.getKey(), values[0]*time, values[1]*time, values[2]*time);
        }
    }

    private float getProgressionValue(List<float[]> progressionPoints, float time) {
        if(time < 0) {
            return 0;
        }
        if(time > 1) {
            return 1;
        }
        for (int i = 0; i < progressionPoints.size() - 1; i++) {
            float[] current = progressionPoints.get(i);
            float[] next = progressionPoints.get(i+1);

            if (time > current[0] && time < next[0]) {
                return 1 - (current[1] + (next[1] - current[1]) * (time - current[0]) / (next[0] - current[0]));
            }
        }

        //Should not occur, but if it does we can just return the base %
        return time;
    }
}
