package com.dumbcodemc.studio.animation.info;

import java.util.ArrayList;
import java.util.List;

public class AnimationInfo {
    private final int version;
    private final List<KeyframeInfo> keyframes = new ArrayList<>();
    private final List<AnimationEventInfo> animationEvents = new ArrayList<>();

    public AnimationInfo(int version) {
        this.version = version;
    }

    public int getVersion() {
        return this.version;
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
