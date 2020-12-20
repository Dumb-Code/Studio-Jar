package net.dumbcode.studio.animation.instance;

import net.dumbcode.studio.animation.info.AnimationEntryData;
import net.dumbcode.studio.animation.info.AnimationInfo;
import net.dumbcode.studio.model.RotationOrder;

import java.util.*;

public class ModelAnimationHandler {

    //This is not final because we can re-use models for culled objects on other objects.
    private Object src;

    private final Map<UUID, AnimationEntry> entries = new HashMap<>();
    private final List<AnimationEntry> cooldownEntries = new ArrayList<>();

    private final Map<String, DelegateCube> cubeDelegates = new HashMap<>();

    public ModelAnimationHandler(RotationOrder order, List<? extends AnimatedCube> allCubes) {
        this(order, allCubes, null);
    }

    public ModelAnimationHandler(RotationOrder order, List<? extends AnimatedCube> allCubes, Object src) {
        for (AnimatedCube cube : allCubes) {
            this.cubeDelegates.put(cube.getInfo().getName(), new DelegateCube(cube, order));
        }
        this.src = src;
    }

    public void setSrc(Object src) {
        this.src = src;
    }

    public void animate(float delta) {
        for (DelegateCube cube : this.cubeDelegates.values()) {
            cube.reset();
        }

        this.cooldownEntries.removeIf(e -> e.cooldownRemove(delta));
        for (AnimationEntry value : this.entries.values()) {
            value.animate(delta);
        }

        for (DelegateCube cube : this.cubeDelegates.values()) {
            cube.apply();
        }
    }

    public UUID startAnimation(AnimationInfo info) {
        return this.startAnimation(new AnimationEntryData(info));
    }

    public UUID startAnimation(AnimationEntryData data) {
        UUID uuid = UUID.randomUUID();
        this.entries.put(uuid, new AnimationEntry(this, data, uuid));
        return uuid;
    }

    public boolean isPlaying(UUID uuid) {
        return this.entries.containsKey(uuid);
    }

    public void markRemoved(UUID uuid) {
        AnimationEntry entry = this.entries.get(uuid);
        if(entry != null) {
            entry.finish();
        }

    }

    void removeEntry(UUID uuid) {
        AnimationEntry remove = this.entries.remove(uuid);
        if(remove != null) {
            this.cooldownEntries.add(remove);
        }
    }

    DelegateCube getCube(String name) {
        return this.cubeDelegates.get(name);
    }

    Object getSrc() {
        return this.src;
    }

}
