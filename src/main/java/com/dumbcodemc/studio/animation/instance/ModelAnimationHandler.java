package com.dumbcodemc.studio.animation.instance;

import com.dumbcodemc.studio.animation.info.AnimationInfo;
import com.dumbcodemc.studio.animation.info.KeyframeInfo;

import java.util.*;

public class ModelAnimationHandler {

    private final Map<UUID, AnimationEntry> entries = new HashMap<>();
    private final Map<String, DelegateCube> nameToCube = new HashMap<>();

    public ModelAnimationHandler(List<? extends AnimatedCube> allCubes) {
        for (AnimatedCube cube : allCubes) {
            this.nameToCube.put(cube.getInfo().getName(), new DelegateCube(cube));
        }
    }

    public void animate(float delta) {
        for (DelegateCube cube : this.nameToCube.values()) {
            cube.reset();
        }
        for (AnimationEntry value : this.entries.values()) {
            value.animate(delta);
        }
        for (DelegateCube cube : this.nameToCube.values()) {
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
        return this.nameToCube.get(name);
    }


}
