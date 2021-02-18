package net.dumbcode.studio.animation.instance;

import net.dumbcode.studio.animation.events.AnimationEventRegister;
import net.dumbcode.studio.animation.info.AnimationEntryData;
import net.dumbcode.studio.animation.info.AnimationEventInfo;

import java.util.*;

public class AnimationEntry extends AnimationConsumer {

    public static float cooldownTime = 0.5F;
    private static final float[] EMPTY = new float[3];

    private final ModelAnimationHandler model;
    private final AnimationEntryData data;
    private final UUID uuid;

    private boolean isLooping;
    private boolean loopEndingMarker;
    private float timeDone;

    private final Map<String, float[]> capturedRotationData = new HashMap<>();
    private final Map<String, float[]> capturedPositionData = new HashMap<>();
    private final Map<String, float[]> capturedCubeGrowData = new HashMap<>();

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

    @Override
    protected void addCubeGrow(String name, float x, float y, float z) {
        DelegateCube cube = this.model.getCube(name);
        if (cube != null) {
            cube.addCubeGrow(x, y, z);
        }
    }

    public void animate(float deltaTime) {
        float previousTime = this.timeDone;
        this.timeDone += deltaTime;
        if(this.isLooping) {
            if(this.timeDone > this.data.getInfo().getLoopingData().getDuration()) {
                this.isLooping = false;
                this.timeDone += this.data.getInfo().getLoopingData().getStart() - this.data.getInfo().getLoopingData().getDuration();
            } else {
                this.animateLoopingFrame();
                return;
            }
        }

        if(this.timeDone > this.data.getInfo().getLoopingData().getEnd()) {
            System.out.println(!this.isLooping + ", " + !this.loopEndingMarker + ", " + this.data.shouldLoop());
            if(!this.isLooping && !this.loopEndingMarker && this.data.shouldLoop()) {
                AnimationCapture.CAPTURE.captureAnimation(this.data.getInfo().getKeyframes(), previousTime, this.capturedPositionData, this.capturedRotationData, this.capturedCubeGrowData);
                this.isLooping = true;
                this.timeDone -= this.data.getInfo().getLoopingData().getEnd();
                this.animateLoopingFrame();
                return;
            }
            this.loopEndingMarker = true;
        }

        boolean finished = this.timeDone > this.data.getInfo().getTotalTime();
        if(finished) {
            AnimationCapture.CAPTURE.captureAnimation(this.data.getInfo().getKeyframes(), previousTime, this.capturedPositionData, this.capturedRotationData, this.capturedCubeGrowData);
            this.finish();
        }

        super.animateAtTime(this.timeDone);

        if(finished) {
            return;
        }

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
            1 - this.timeDone/this.data.getInfo().getLoopingData().getDuration(),
            this.data.getInfo().getLoopedKeyframe().getPositionMap(),
            this.data.getInfo().getLoopedKeyframe().getRotationMap(),
            this.data.getInfo().getLoopedKeyframe().getCubeGrowMap()
        );
    }

    //True if finished. False otherwise.
    public boolean cooldownRemove(float deltaTime) {
        this.timeDone -= deltaTime;
        if (this.timeDone <= 0) {
            return true;
        }
        this.renderFromCaptured(this.timeDone / cooldownTime, Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap());
        return false;


    }
    public void renderFromCaptured(float time, Map<String, float[]> posOffset, Map<String, float[]> rotOffset, Map<String, float[]> cubeGrowOff) {
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
        this.capturedCubeGrowData.forEach((name, data) -> {
            DelegateCube cube = this.model.getCube(name);
            float[] off = cubeGrowOff.getOrDefault(name, EMPTY);
            if (cube != null) {
                cube.addCubeGrow(
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
