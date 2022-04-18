package net.dumbcode.studio.animation.info;

import java.util.function.Supplier;

public class AnimationEntryData {
    private final AnimationInfo info;
    private Supplier<Boolean> loopUntil = () -> true;
    private Supplier<Boolean> holdUntil = () -> true;

    private Supplier<Float> speedSupplier = () -> 1F;
    private Supplier<Float> degreeFactorSupplier = () -> 1F;

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
        return this.withSpeed(() -> speed);
    }
    public AnimationEntryData withSpeed(Supplier<Float> speed) {
        this.speedSupplier = speed;
        return this;
    }


    public AnimationEntryData withDegreeFactor(float degreeFactor) {
        return this.withDegreeFactor(() -> degreeFactor);
    }

    public AnimationEntryData withDegreeFactor(Supplier<Float> degreeFactor) {
        this.degreeFactorSupplier = degreeFactor;
        return this;
    }

    public boolean shouldLoop() {
        return !this.loopUntil.get();
    }

    public Supplier<Boolean> getLoopUntil() {
        return loopUntil;
    }

    public boolean shouldHold() {
        return !this.holdUntil.get();
    }

    public Supplier<Boolean> getHoldUntil() {
        return holdUntil;
    }

    public float getSpeed() {
        return this.speedSupplier.get();
    }

    public Supplier<Float> getSpeedSupplier() {
        return speedSupplier;
    }

    public float getDegreeFactor() {
        return degreeFactorSupplier.get();
    }

    public Supplier<Float> getDegreeFactorSupplier() {
        return degreeFactorSupplier;
    }

    public AnimationInfo getInfo() {
        return info;
    }
}
