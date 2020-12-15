package net.dumbcode.studio.animation.info;

import net.dumbcode.studio.model.RotationOrder;

import java.util.ArrayList;
import java.util.List;

public class AnimationInfo {
    private final int version;
    private final RotationOrder order;
    private final List<KeyframeInfo> keyframes = new ArrayList<>();
    private final List<AnimationEventInfo> animationEvents = new ArrayList<>();

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

    @Override
    public String toString() {
        return "AnimationInfo{" +
            "version=" + version +
            ", keyframes=" + keyframes +
            ", animationEvents=" + animationEvents +
            '}';
    }
}
