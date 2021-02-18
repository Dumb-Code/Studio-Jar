package net.dumbcode.studio.animation.info;

import net.dumbcode.studio.animation.instance.AnimationCapture;
import net.dumbcode.studio.model.RotationOrder;

import java.util.*;
import java.util.stream.IntStream;

public class AnimationInfo {

    private static final AnimationEventInfo[] EMPTY = new AnimationEventInfo[0];

    private final int version;
    private final RotationOrder order;
    //Nullable
    private KeyframeHeader.LoopingData loopingData;
    private final List<KeyframeInfo> keyframes = new ArrayList<>();
    private final List<AnimationEventInfo> animationEvents = new ArrayList<>();
    private float totalTime = 0;
//    private float loopStartTime;

    private KeyframeInfo loopedKeyframe;

    private AnimationEventInfo[][] sortedEvents;

    public AnimationInfo(int version, RotationOrder order, KeyframeHeader.LoopingData loopingData) {
        this.version = version;
        this.order = order;
        this.loopingData = loopingData;
    }

    public int getVersion() {
        return this.version;
    }

    public RotationOrder getOrder() {
        return order;
    }

    public List<KeyframeInfo> getKeyframes() {
        return this.keyframes;
    }

    public List<AnimationEventInfo> getAnimationEvents() {
        return animationEvents;
    }

    public void generatedCachedData() {
        this.totalTime = (float) this.keyframes.stream().mapToDouble(kf -> kf.getStartTime() + kf.getDuration()).max().orElseThrow(NoSuchElementException::new);

        this.sortedEvents = IntStream.range(0, (int)this.totalTime + 1)
            .mapToObj(i ->
                this.animationEvents.stream()
                    .filter(e -> i == (int)e.getTime())
                    .toArray(value -> value == 0 ? EMPTY : new AnimationEventInfo[value])
            )
            .toArray(AnimationEventInfo[][]::new);

        if(this.loopingData != null) {
            this.recalculateLooping();
        }
    }

    public float getTotalTime() {
        return totalTime;
    }

    public KeyframeHeader.LoopingData getLoopingData() {
        return loopingData;
    }

    public AnimationInfo setLoopStartTime(float loopStartTime) {
        return this;
    }

    public AnimationInfo recalculateLooping() {
        if(this.loopingData == null) {
            return this;
        }

        this.loopedKeyframe = new KeyframeInfo(this.loopingData.getEnd(), this.loopingData.getDuration(), -1);

//        Map<String, float[]> positionMap = new HashMap<>();
//        Map<String, float[]> rotationMap = new HashMap<>();
//        Map<String, float[]> cubeGrowMap = new HashMap<>();

        AnimationCapture.CAPTURE.captureAnimation(this.getKeyframes(), this.loopingData.getStart(), this.loopedKeyframe.getPositionMap(), this.loopedKeyframe.getRotationMap(), this.loopedKeyframe.getCubeGrowMap());
//        AnimationCapture.CAPTURE.captureAnimation(this.getKeyframes(), this.loopingData.getStart(), positionMap, rotationMap, cubeGrowMap);

        return this;
    }

    private void ensureLoopingData() {
        if(this.loopingData == null) {
            this.loopingData = new KeyframeHeader.LoopingData(
                this.totalTime,
                this.keyframes.stream()
                    .min(Comparator.comparingDouble(KeyframeInfo::getStartTime))
                    .map(kf -> kf.getStartTime() + kf.getDuration())
                    .orElse(0F),
                5F);
        }
    }

    public AnimationInfo setLoopingStart(float start) {
        this.ensureLoopingData();
        this.loopingData.setStart(start);
        this.recalculateLooping();
        return this;
    }

    public AnimationInfo setLoopingEnd(float end) {
        this.ensureLoopingData();
        this.loopingData.setEnd(end);
        this.recalculateLooping();
        return this;
    }

    public AnimationInfo setLoopingDuration(float duration) {
        this.ensureLoopingData();
        this.loopingData.setDuration(duration);
        this.recalculateLooping();
        return this;
    }

    public AnimationInfo setLoopingData(float start, float end, float duration) {
        this.loopingData = new KeyframeHeader.LoopingData(start, end, duration);
        return this;
    }

    public KeyframeInfo getLoopedKeyframe() {
        return loopedKeyframe;
    }

    public AnimationEventInfo[][] getSortedEvents() {
        return sortedEvents;
    }


    public AnimationEntryData data() {
        return new AnimationEntryData(this);
    }

    @Override
    public String toString() {
        return "AnimationInfo{" +
            "version=" + version +
            ", loopingData=" + loopingData +
            ", keyframes=" + keyframes.size() +
            ", animationEvents=" + animationEvents.size() +
            ", totalTime=" + totalTime +
            '}';
    }
}
