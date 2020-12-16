package net.dumbcode.studio.animation.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimationEventRegister {
    private static final Map<String, List<AnimationEventHandler>> EVENT_MAP = new HashMap<>();

    public static void registerEvent(String type, AnimationEventHandler handler) {
        EVENT_MAP.computeIfAbsent(type, k -> new ArrayList<>()).add(handler);
    }

    public static void playEvent(String type, String data, Object src) {
        if(EVENT_MAP.containsKey(type)) {
            for (AnimationEventHandler handler : EVENT_MAP.get(type)) {
                handler.eventPlayed(data, src);
            }
        }
    }
}
