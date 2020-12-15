package com.dumbcodemc.studio.animation.info;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimationEventInfo {
    private final int time;
    private final Map<String, List<String>> data = new HashMap<>();

    public AnimationEventInfo(int time) {
        this.time = time;
    }

    public int getTime() {
        return this.time;
    }

    public Map<String, List<String>> getData() {
        return this.data;
    }
}
