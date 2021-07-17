package net.dumbcode.studio.animation.instance;

import net.dumbcode.studio.animation.info.AnimationEntryData;
import net.dumbcode.studio.animation.info.AnimationInfo;
import net.dumbcode.studio.model.RotationOrder;

import java.sql.Array;
import java.util.*;

public class ModelAnimationHandler {

    //This is not final because we can re-use models for culled objects on other objects.
    private Object src;

    private final Map<UUID, AnimationEntry> entries = new HashMap<>();
    private final List<UUID> entriesToRemove = new ArrayList<>();
    private final List<AnimationEntry> cooldownEntries = new ArrayList<>();

    private final Map<String, DelegateCube> cubeDelegates = new HashMap<>();

    public ModelAnimationHandler() {
        this(null);
    }

    public ModelAnimationHandler(Object src) {
        this.src = src;
    }

    public void setSrc(Object src) {
        this.src = src;
    }

    //delta -> seconds
    public void animate(Iterable<? extends AnimatedCube> cubes, float delta) {
        for (DelegateCube cube : this.cubeDelegates.values()) {
            cube.reset();
        }

        this.cooldownEntries.removeIf(e -> e.cooldownRemove(delta));
        for (AnimationEntry value : this.entries.values()) {
            value.animate(delta);
        }

        for (AnimatedCube cube : cubes) {
            this.getCube(cube.getInfo().getName()).apply(cube);
        }

        this.entriesToRemove.forEach(this.entries::remove);
    }

    public UUID startAnimation(AnimationInfo info) {
        return this.startAnimation(new AnimationEntryData(info));
    }

    public UUID startAnimation(AnimationEntryData data) {
        UUID uuid = UUID.randomUUID();
        this.entries.put(uuid, new AnimationEntry(this, data, uuid));
        return uuid;
    }

    public void markAllRemoved() {
        for (AnimationEntry value : this.entries.values()) {
            value.finish();
        }
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

    public void forceAnimation(UUID uuid, AnimationInfo info) {
        AnimationEntry entry = this.entries.get(uuid);
        if(entry != null) {
            entry.forceAnimation(info);
        }
    }

    public AnimationInfo getInfo(UUID uuid) {
        AnimationEntry entry = this.entries.get(uuid);
        if(entry != null) {
            return entry.getData().getInfo();
        }
        return null;
    }

    void removeEntry(UUID uuid) {
        AnimationEntry remove = this.entries.get(uuid);
        this.entriesToRemove.add(uuid);
        if(remove != null) {
            this.cooldownEntries.add(remove);
        }
    }

    DelegateCube getCube(String name) {
        return this.cubeDelegates.computeIfAbsent(name, n -> new DelegateCube());
    }

    Object getSrc() {
        return this.src;
    }

}
