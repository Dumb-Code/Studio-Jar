package net.dumbcode.studio.animation.info;

public class AnimationEntryData {
    private final AnimationInfo info;
    private boolean loop;

    public AnimationEntryData(AnimationInfo info) {
        this.info = info;
    }

    public AnimationEntryData loop() {
        this.loop = true;
        return this;
    }

    public boolean isLoop() {
        return loop;
    }

    public AnimationInfo getInfo() {
        return info;
    }
}
