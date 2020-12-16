package net.dumbcode.studio.animation.info;

import net.dumbcode.studio.model.RotationOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class AnimationInfo {

    private static final AnimationEventInfo[] EMPTY = new AnimationEventInfo[0];

    private final int version;
    private final RotationOrder order;
    private final List<KeyframeInfo> keyframes = new ArrayList<>();
    private final List<AnimationEventInfo> animationEvents = new ArrayList<>();

    private float totalTime = 0;
    private AnimationEventInfo[][] sortedEvents;

    public AnimationInfo(int version, RotationOrder order) {
        this.version = version;
        this.order = order;
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
    }

    public float getTotalTime() {
        return totalTime;
    }

    public AnimationEventInfo[][] getSortedEvents() {
        return sortedEvents;
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
