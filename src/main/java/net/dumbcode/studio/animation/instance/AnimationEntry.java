package net.dumbcode.studio.animation.instance;

import net.dumbcode.studio.animation.events.AnimationEventRegister;
import net.dumbcode.studio.animation.info.AnimationEntryData;
import net.dumbcode.studio.animation.info.AnimationEventInfo;
import net.dumbcode.studio.animation.info.AnimationInfo;
import net.dumbcode.studio.animation.info.KeyframeInfo;

import java.util.*;

public class AnimationEntry extends AnimationConsumer {

    public static float cooldownTime = 10;
    private static final float[] EMPTY = new float[3];

    private final ModelAnimationHandler model;
    private final AnimationEntryData data;
    private final UUID uuid;

    private boolean isLooping;
    private float timeDone;

    private final Map<String, float[]> capturedRotationData = new HashMap<>();
    private final Map<String, float[]> capturedPositionData = new HashMap<>();

    public AnimationEntry(ModelAnimationHandler model, AnimationEntryData data, UUID uuid) {
        super(data.getInfo().getKeyframes());
        this.model = model;
        this.data = data;
        this.uuid = uuid;
    }

    @Override
    protected void addPosition(String name, float x, float y, float z) {
        DelegateCube cube = this.model.getCube(name);
        if (cube != null) {
            cube.addPosition(x, y, z);
        }
    }

    @Override
    protected void addRotation(String name, float x, float y, float z) {
        DelegateCube cube = this.model.getCube(name);
        if (cube != null) {
            cube.addRotation(this.data.getInfo().getOrder(), x, y, z);
        }
    }

    public void animate(float deltaTime) {
        float previousTime = this.timeDone;
        this.timeDone += deltaTime;
        if(this.isLooping) {
            if(this.timeDone > this.data.getInfo().getLoopStartTime()) {
                this.isLooping = false;
            } else {
                this.animateLoopingFrame();
                return;
            }
        }
        if(this.timeDone > this.data.getInfo().getTotalTime()) {
            AnimationCapture.CAPTURE.captureAnimation(this.data.getInfo().getKeyframes(), previousTime, this.capturedPositionData, this.capturedRotationData);
            if(!this.data.isLoop()) {
                this.isLooping = true;
                this.timeDone = 0;
            } else {
                this.finish();
            }
            return;
        }

        super.animateAtTime(this.timeDone);

        for (AnimationEventInfo event : this.data.getInfo().getSortedEvents()[(int) this.timeDone]) {
            if(event.getTime() >= previousTime && event.getTime() < this.timeDone) {
                for (Map.Entry<String, List<String>> entry : event.getData().entrySet()) {
                    for (String s : entry.getValue()) {
                        AnimationEventRegister.playEvent(entry.getKey(), s, this.model.getSrc());
                    }
                }
            }
        }
    }

    private void animateLoopingFrame() {
        this.renderFromCaptured (
            1 - this.timeDone/this.data.getInfo().getLoopStartTime(),
            this.data.getInfo().getLoopedKeyframe().getPositionMap(),
            this.data.getInfo().getLoopedKeyframe().getRotationMap()
        );
    }

    //True if finished. False otherwise.
    public boolean cooldownRemove(float deltaTime) {
        this.timeDone -= deltaTime;
        if (this.timeDone <= 0) {
            return true;
        }
        this.renderFromCaptured(this.timeDone / cooldownTime, Collections.emptyMap(), Collections.emptyMap());
        return false;


    }
    public void renderFromCaptured(float time, Map<String, float[]> posOffset, Map<String, float[]> rotOffset) {
        float invTime = 1 - time;
        this.capturedPositionData.forEach((name, data) -> {
            DelegateCube cube = this.model.getCube(name);
            float[] off = posOffset.getOrDefault(name, EMPTY);
            if(cube != null) {
                cube.addPosition(
                    data[0]*time + off[0]*invTime,
                    data[1]*time + off[1]*invTime,
                    data[2]*time + off[2]*invTime
                );
            }
        });
        this.capturedRotationData.forEach((name, data) -> {
            DelegateCube cube = this.model.getCube(name);
            float[] off = rotOffset.getOrDefault(name, EMPTY);
            if (cube != null) {
                cube.addRotation(this.data.getInfo().getOrder(),
                    data[0]*time + off[0]*invTime,
                    data[1]*time + off[1]*invTime,
                    data[2]*time + off[2]*invTime
                );
            }
        });
    }

    public void finish() {
        this.timeDone = cooldownTime;
        this.model.removeEntry(this.uuid);
    }

    public UUID getUuid() {
        return uuid;
    }
}
