package net.dumbcode.studio.animation.instance;

import net.dumbcode.studio.animation.info.AnimationEntryData;
import net.dumbcode.studio.animation.info.AnimationInfo;
import net.dumbcode.studio.model.RotationOrder;

import java.util.*;

public class ModelAnimationHandler {

    //This is not final because we can re-use models for culled objects on other objects.
    private Object src;

    private final RotationOrder order;

    private final Map<UUID, AnimationEntry> entries = new HashMap<>();
    private final List<AnimationEntry> cooldownEntries = new ArrayList<>();

    private final Map<String, DelegateCube> cubeDelegates = new HashMap<>();

    public ModelAnimationHandler() {
        this(RotationOrder.ZYX);
    }

    public ModelAnimationHandler(RotationOrder order) {
        this(order, null);
    }

    public ModelAnimationHandler(RotationOrder order, Object src) {
        this.order = order;
        this.src = src;
    }

    public void setSrc(Object src) {
        this.src = src;
    }

    //delta -> seconds
    public void animate(List<AnimatedCube> cubes, float delta) {
        for (DelegateCube cube : this.cubeDelegates.values()) {
            cube.reset();
        }

        this.cooldownEntries.removeIf(e -> e.cooldownRemove(delta));
        for (AnimationEntry value : this.entries.values()) {
            value.animate(delta);
        }

        for (AnimatedCube cube : cubes) {
            DelegateCube delegateCube = this.getCube(cube.getInfo().getName());
            if(delegateCube != null) {
                delegateCube.apply(cube);
            }
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
        return this.cubeDelegates.computeIfAbsent(name, n -> new DelegateCube(this.order));
    }

    Object getSrc() {
        return this.src;
    }

}
