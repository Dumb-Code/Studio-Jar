package net.dumbcode.studio.animation.info;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimationEventInfo {
    private final float time;
    private final Map<String, List<String>> data = new HashMap<>();

    public AnimationEventInfo(float time) {
        this.time = time;
    }

    public float getTime() {
        return this.time;
    }

    public Map<String, List<String>> getData() {
        return this.data;
    }
}
