package net.dumbcode.studio.animation.info;

import java.util.function.Supplier;

public class AnimationEntryData {
    private final AnimationInfo info;
    private Supplier<Boolean> loopUntil = () -> false;

    public AnimationEntryData(AnimationInfo info) {
        this.info = info;
    }

    public AnimationEntryData loopForever() {
        return this.loopUntil(() -> true);
    }

    public AnimationEntryData loopUntil(Supplier<Boolean> condition) {
        this.loopUntil = condition;
        return this;
    }

    public boolean shouldLoop() {
        return !this.loopUntil.get();
    }

    public AnimationInfo getInfo() {
        return info;
    }
}
