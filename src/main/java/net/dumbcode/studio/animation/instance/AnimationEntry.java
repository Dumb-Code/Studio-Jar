package net.dumbcode.studio.animation.instance;

import net.dumbcode.studio.animation.info.AnimationInfo;
import net.dumbcode.studio.animation.info.KeyframeInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AnimationEntry {

    public static float cooldownTime = 10;

    private final ModelAnimationHandler model;
    private final AnimationInfo info;
    private final UUID uuid;
    private float timeDone;

    private boolean markedRemove;

    private final Map<String, float[]> endRotationData = new HashMap<>();
    private final Map<String, float[]> endPositionData = new HashMap<>();

    public AnimationEntry(ModelAnimationHandler model, AnimationInfo info, UUID uuid) {
        this.model = model;
        this.info = info;
        this.uuid = uuid;
    }

    public void animate(float deltaTime) {
        if(!this.markedRemove) {
            this.timeDone += deltaTime;
        }

        boolean done = true;
        for (KeyframeInfo keyframe : this.info.getKeyframes()) {
            done &= this.animateKeyframe(keyframe);
        }

        if (this.markedRemove) {
            this.timeDone = cooldownTime;
            this.model.removeEntry(this.uuid);
        }

        if (done) {
            this.markedRemove = true;
        }

        //TODO: animation events.
    }

    //True if finished. False otherwise.
    public boolean cooldownRemove(float deltaTime) {
        this.timeDone -= deltaTime;
        if (this.timeDone <= 0) {
            return true;
        }
        float time = this.timeDone/cooldownTime;
        this.endPositionData.forEach((name, data) -> {
            DelegateCube cube = this.model.getCube(name);
            if(cube != null) {
                cube.addPosition(
                    data[0] * time,
                    data[1] * time,
                    data[2] * time
                );
            }
        });
        this.endRotationData.forEach((name, data) -> {
            DelegateCube cube = this.model.getCube(name);
            if (cube != null) {
                cube.addRotation(this.info.getOrder(),
                    data[0] * time,
                    data[1] * time,
                    data[2] * time
                );
            }
        });
        return false;
    }

    //returns true if finished, false if not
    private boolean animateKeyframe(KeyframeInfo info) {
        float localTimeDone = (this.timeDone - info.getStartTime()) / info.getDuration();

        if(localTimeDone <= 0) {
            return false;
        }

        float time;
        if(localTimeDone >= 1) {
            time = 1;
        } else {
            time = this.getProgressionValue(info, localTimeDone);
        }


        String cubeName;
        float[] value;
        DelegateCube cube;

        for (Map.Entry<String, float[]> entry : info.getRotationMap().entrySet()) {
            cubeName = entry.getKey();
            value = entry.getValue();
            cube = this.model.getCube(cubeName);
            if (cube != null) { //When an animation references a cube that doesn't exist
                if (this.markedRemove) {
                    float[] data = this.endRotationData.computeIfAbsent(cubeName, k -> new float[3]);
                    data[0] += value[0] * time;
                    data[1] += value[1] * time;
                    data[2] += value[2] * time;
                }

                cube.addRotation(this.info.getOrder(),
                        value[0] * time,
                        value[1] * time,
                        value[2] * time
                );
            }
        }

        for (Map.Entry<String, float[]> entry : info.getPositionMap().entrySet()) {
            cubeName = entry.getKey();
            value = entry.getValue();
            cube = this.model.getCube(cubeName);
            if (cube != null) { //When an animation references a cube that doesn't exist
                if (this.markedRemove) {
                    float[] data = this.endPositionData.computeIfAbsent(cubeName, k -> new float[3]);
                    data[0] += value[0] * time;
                    data[1] += value[1] * time;
                    data[2] += value[2] * time;
                }
                cube.addPosition(
                        value[0] * time,
                        value[1] * time,
                        value[2] * time
                );
            }
        }

        return time == 1;
    }

    private float getProgressionValue(KeyframeInfo info, float timeDone) {
        for (int i = 0; i < info.getProgressionPoints().size() - 1; i++) {
            float[] current = info.getProgressionPoints().get(i);
            float[] next = info.getProgressionPoints().get(i+1);

            if (timeDone > current[0] && timeDone < next[0]) {
                return 1 - (current[1] + (next[1] - current[1]) * (timeDone - current[0]) / (next[0] - current[0]));
            }
        }

        //Should not occur, but if it does we can just return the base %
        return timeDone;
    }


    public UUID getUuid() {
        return uuid;
    }
}
