package net.dumbcode.studio.animation.info;

import net.dumbcode.studio.animation.instance.AnimationCapture;
import net.dumbcode.studio.animation.instance.AnimationEntry;
import net.dumbcode.studio.model.RotationOrder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class AnimationInfo {

    private static final AnimationEventInfo[] EMPTY = new AnimationEventInfo[0];

    private final int version;
    private final RotationOrder order;
    private final List<KeyframeInfo> keyframes = new ArrayList<>();
    private final List<AnimationEventInfo> animationEvents = new ArrayList<>();
    private float totalTime = 0;
    private float loopStartTime;

    private KeyframeInfo loopedKeyframe;

    private AnimationEventInfo[][] sortedEvents;

    public AnimationInfo(int version, RotationOrder order) {
        this(version, order, -1);
    }

    public AnimationInfo(int version, RotationOrder order, float loopStartTime) {
        this.version = version;
        this.order = order;
        this.loopStartTime = loopStartTime;
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
        this.totalTime = (float) this.keyframes.stream().mapToDouble(kf -> kf.getStartTime() + kf.getDuration()).max().orElseThrow();

        this.sortedEvents = IntStream.range(0, (int)this.totalTime + 1)
            .mapToObj(i ->
                this.animationEvents.stream()
                    .filter(e -> i == (int)e.getTime())
                    .toArray(value -> value == 0 ? EMPTY : new AnimationEventInfo[value])
            )
            .toArray(AnimationEventInfo[][]::new);


        if(this.loopStartTime == -1) {
            this.loopStartTime = this.keyframes.stream()
                .min(Comparator.comparingDouble(KeyframeInfo::getStartTime))
                .map(kf -> kf.getStartTime() + kf.getDuration())
                .orElse(-1F);
        }
        this.setLoopStartTime(this.loopStartTime);
    }

    public float getTotalTime() {
        return totalTime;
    }

    public AnimationInfo setLoopStartTime(float loopStartTime) {
        this.loopStartTime = loopStartTime;
        this.loopedKeyframe = new KeyframeInfo(0, loopStartTime, -1);
        AnimationCapture.CAPTURE.captureAnimation(this.getKeyframes(), loopStartTime, this.loopedKeyframe.getPositionMap(), this.loopedKeyframe.getRotationMap());
        return this;
    }

    public float getLoopStartTime() {
        return loopStartTime;
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
            ", keyframes=" + keyframes +
            ", animationEvents=" + animationEvents +
            '}';
    }
}
