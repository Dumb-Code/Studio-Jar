package net.dumbcode.studio.animation.info;

import java.util.*;

public class KeyframeInfo {
    private final float startTime;
    private final float duration;
    private final int layer;
    private final Map<String, float[]> rotationMap = new HashMap<>();
    private final Map<String, float[]> positionMap = new HashMap<>();
    private final List<float[]> progressionPoints = new ArrayList<>();

    public KeyframeInfo(float startTime, float duration, int layer) {
        this.startTime = startTime;
        this.duration = duration;
        this.layer = layer;
    }

    public float getStartTime() {
        return this.startTime;
    }

    public float getDuration() {
        return this.duration;
    }

    public int getLayer() {
        return this.layer;
    }

    public Map<String, float[]> getRotationMap() {
        return rotationMap;
    }

    public Map<String, float[]> getPositionMap() {
        return this.positionMap;
    }

    public List<float[]> getProgressionPoints() {
        return this.progressionPoints;
    }

    @Override
    public String toString() {
        return "KeyframeInfo{" +
            "startTime=" + startTime +
            ", duration=" + duration +
            ", layer=" + layer +
            ", rotationMap=" + rotationMap.size() +
            ", positionMap=" + positionMap.size() +
            ", progressionPoints=" + progressionPoints.size() +
            '}';
    }
}
