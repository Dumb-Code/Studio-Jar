package net.dumbcode.studio.animation.instance;

import net.dumbcode.studio.animation.info.AnimationInfo;
import net.dumbcode.studio.model.RotationOrder;

import java.util.*;

public class ModelAnimationHandler {

    private final RotationOrder order;
    private final Map<UUID, AnimationEntry> entries = new HashMap<>();

    private final Map<String, DelegateCubeRotation> rotationDelegates = new HashMap<>();
    private final Map<String, DelegateCubePosition> positionDelegates = new HashMap<>();
    public ModelAnimationHandler(RotationOrder order, List<? extends AnimatedCube> allCubes) {
        this.order = order;
        for (AnimatedCube cube : allCubes) {
            this.rotationDelegates.put(cube.getInfo().getName(), new DelegateCubeRotation(cube, order));
            this.positionDelegates.put(cube.getInfo().getName(), new DelegateCubePosition(cube));
        }
    }

    public void animate(float delta) {
        for (DelegateCubeRotation cube : this.rotationDelegates.values()) {
            cube.reset();
        }
        for (DelegateCubePosition cube : this.positionDelegates.values()) {
            cube.reset();
        }

        for (AnimationEntry value : this.entries.values()) {
            value.animate(delta);
        }

        for (DelegateCubeRotation cube : this.rotationDelegates.values()) {
            cube.apply();
        }
        for (DelegateCubePosition cube : this.positionDelegates.values()) {
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

    DelegateCubeRotation getRotationCube(String name) {
        return this.rotationDelegates.get(name);
    }

    DelegateCubePosition getPositionCube(String name) {
        return this.positionDelegates.get(name);
    }

}
