package net.dumbcode.studio.animation.info;

import net.dumbcode.studio.animation.instance.AnimationCapture;
import net.dumbcode.studio.model.RotationOrder;

import java.util.*;
import java.util.stream.IntStream;

public class AnimationInfo {

    private static final AnimationEventInfo[] EMPTY_EVENT_INFO = new AnimationEventInfo[0];
    public static final AnimationInfo EMPTY = new AnimationInfo(-1, RotationOrder.global, null);

    private final int version;
    private final RotationOrder order;
    //Nullable
    private final boolean shouldComputeLoopingData;
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
        this.shouldComputeLoopingData = loopingData != null;
        this.generatedCachedData();
    }

    public int getVersion() {
        return this.version;
    }

    public RotationOrder getOrder() {
        return order;
    }

    public void addKeyframe(KeyframeInfo info) {
        this.keyframes.add(info);
    }

    public List<KeyframeInfo> getKeyframes() {
        return Collections.unmodifiableList(this.keyframes);
    }

    public List<AnimationEventInfo> getAnimationEvents() {
        return animationEvents;
    }

    public void generatedCachedData() {
        this.totalTime = (float) this.keyframes.stream().mapToDouble(kf -> kf.getStartTime() + kf.getDuration()).max().orElse(0);

        this.sortedEvents = IntStream.range(0, (int)this.totalTime + 1)
            .mapToObj(i ->
                this.animationEvents.stream()
                    .filter(e -> i == (int)e.getTime())
                    .toArray(value -> value == 0 ? EMPTY_EVENT_INFO : new AnimationEventInfo[value])
            )
            .toArray(AnimationEventInfo[][]::new);

        this.ensureLoopingData();
        this.recalculateLooping();
    }

    public float getTotalTime() {
        return totalTime;
    }

    public KeyframeHeader.LoopingData getLoopingData() {
        return loopingData;
    }

    public AnimationInfo recalculateLooping() {
        if(this.loopingData == null) {
            return this;
        }

        this.loopedKeyframe = new KeyframeInfo(this.loopingData.getEnd(), this.loopingData.getDuration(), -1);

        AnimationCapture.CAPTURE.captureAnimation(this.getKeyframes(), this.loopingData.getStart(), this.loopedKeyframe.getPositionMap(), this.loopedKeyframe.getRotationMap(), this.loopedKeyframe.getCubeGrowMap());
        return this;
    }

    private void ensureLoopingData() {
        if(this.shouldComputeLoopingData) {
            this.loopingData = new KeyframeHeader.LoopingData(
                this.keyframes.stream()
                    .min(Comparator.comparingDouble(KeyframeInfo::getStartTime))
                    .map(kf -> kf.getStartTime() + kf.getDuration())
                    .orElse(0F),
                this.totalTime,
                0.5F);
        }
    }

    public AnimationInfo setLoopingStart(float start) {
        this.loopingData.setStart(start);
        this.recalculateLooping();
        return this;
    }

    public AnimationInfo setLoopingEnd(float end) {
        this.loopingData.setEnd(end);
        this.recalculateLooping();
        return this;
    }

    public AnimationInfo setLoopingDuration(float duration) {
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
