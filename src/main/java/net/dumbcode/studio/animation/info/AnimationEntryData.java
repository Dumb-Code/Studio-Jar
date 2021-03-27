package net.dumbcode.studio.animation.info;

import java.util.function.Supplier;

public class AnimationEntryData {
    private final AnimationInfo info;
    private Supplier<Boolean> loopUntil = () -> true;
    private Supplier<Boolean> holdUntil = () -> false;

    private float speed;
    private float degreeFactor;

    public AnimationEntryData(AnimationInfo info) {
        this.info = info;
    }

    public AnimationEntryData loopForever() {
        return this.loopUntil(() -> false);
    }
    public AnimationEntryData holdForever() {
        return this.holdUntil(() -> false);
    }

    public AnimationEntryData loopUntil(Supplier<Boolean> condition) {
        this.loopUntil = condition;
        return this;
    }
    public AnimationEntryData holdUntil(Supplier<Boolean> condition) {
        this.holdUntil = condition;
        return this;
    }
    public AnimationEntryData withSpeed(float speed) {
        this.speed = speed;
        return this;
    }
    public AnimationEntryData withDegreeFactor(float degreeFactor) {
        this.degreeFactor = degreeFactor;
        return this;
    }

    public boolean shouldLoop() {
        return !this.loopUntil.get();
    }
    public boolean shouldHold() {
        return this.holdUntil.get();
    }
    public float getSpeed() {
        return this.speed;
    }

    public float getDegreeFactor() {
        return degreeFactor;
    }

    public AnimationInfo getInfo() {
        return info;
    }
}
