package net.dumbcode.studio.animation.instance;

import net.dumbcode.studio.animation.info.AnimationInfo;
import net.dumbcode.studio.animation.info.KeyframeInfo;

import java.util.UUID;

public class AnimationEntry {
    private final ModelAnimationHandler model;
    private final AnimationInfo info;
    private final UUID uuid;
    private float timeDone;

    public AnimationEntry(ModelAnimationHandler model, AnimationInfo info, UUID uuid) {
        this.model = model;
        this.info = info;
        this.uuid = uuid;
    }

    public void animate(float deltaTime) {
        this.timeDone += deltaTime;
        boolean done = true;
        for (KeyframeInfo keyframe : this.info.getKeyframes()) {
            done &= this.animateKeyframe(keyframe);
        }

        if(done) {
            this.model.removeEntry(this.uuid);
        }

        //TODO: animation events.
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

        info.getRotationMap().forEach((cubeName, values) -> {
            DelegateCube cube = this.model.getCube(cubeName);
            if(cube != null) { //When an animation references a cube that doesn't exist
                cube.addRotation(this.info.getOrder(),
                    values[0] * time,
                    values[1] * time,
                    values[2] * time
                );
            }
        });

        info.getPositionMap().forEach((cubeName, values) -> {
            DelegateCube cube = this.model.getCube(cubeName);
            if(cube != null) { //When an animation references a cube that doesn't exist
                cube.addPosition(
                    values[0] * time,
                    values[1] * time,
                    values[2] * time
                );
            }
        });

        return time == 1;
    }

    private float getProgressionValue(KeyframeInfo info, float timeDone) {
        for (int i = 0; i < info.getProgressionPoints().size() - 1; i++) {
            float[] current = info.getProgressionPoints().get(i);
            float[] next = info.getProgressionPoints().get(i+1);

            if(timeDone > current[0] && timeDone < next[0]) {
                float interpolateBetweenAmount = (timeDone - current[0]) / (next[0] - current[0]);
                return 1 - (current[1] + (next[1] - current[1]) * interpolateBetweenAmount);
            }
        }

        //Should not occur, but if it does we can just return the base %
        return timeDone;
    }

    public UUID getUuid() {
        return uuid;
    }
}
