package net.dumbcode.studio.animation.info;

public class AnimationEntryData {
    private final AnimationInfo info;
    private boolean loop;

    public AnimationEntryData(AnimationInfo info) {
        this.info = info;
    }

    public AnimationEntryData loop() {
        return this.loop(true);
    }

    public AnimationEntryData loop(boolean loop) {
        this.loop = loop;
        return this;
    }

    public boolean isLoop() {
        return loop;
    }

    public AnimationInfo getInfo() {
        return info;
    }
}
