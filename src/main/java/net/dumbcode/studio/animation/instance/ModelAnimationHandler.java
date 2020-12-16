package net.dumbcode.studio.animation.instance;

import net.dumbcode.studio.animation.info.AnimationInfo;
import net.dumbcode.studio.model.RotationOrder;

import java.util.*;

public class ModelAnimationHandler {

    private final RotationOrder order;
    private final Map<UUID, AnimationEntry> entries = new HashMap<>();

    private final Map<String, DelegateCube> cubeDelegates = new HashMap<>();
    
    public ModelAnimationHandler(RotationOrder order, List<? extends AnimatedCube> allCubes) {
        this.order = order;
        for (AnimatedCube cube : allCubes) {
            this.cubeDelegates.put(cube.getInfo().getName(), new DelegateCube(cube, order));
        }
    }

    public void animate(float delta) {
        for (DelegateCube cube : this.cubeDelegates.values()) {
            cube.reset();
        }

        for (AnimationEntry value : this.entries.values()) {
            value.animate(delta);
        }

        for (DelegateCube cube : this.cubeDelegates.values()) {
            cube.apply();
        }
    }


    public UUID startAnimation(AnimationInfo info) {
        UUID uuid = UUID.randomUUID();
        this.entries.put(uuid, new AnimationEntry(this, info, uuid));
        //TODO: gather removed animations and interpolate it to 0 (ghost wraps)
        return uuid;
    }

    public boolean isPlaying(UUID uuid) {
        return this.entries.containsKey(uuid);
    }

    public void removeEntry(UUID uuid) {
        this.entries.remove(uuid);
    }

    DelegateCube getCube(String name) {
        return this.cubeDelegates.get(name);
    }

}
