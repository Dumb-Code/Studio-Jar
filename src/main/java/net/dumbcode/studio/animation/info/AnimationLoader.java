package net.dumbcode.studio.animation.info;

import net.dumbcode.studio.model.RotationOrder;
import net.dumbcode.studio.util.StudioInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class AnimationLoader {
    public static int MINIMUM_VERSION = 6;
    public static int MAXIMUM_VERSION = 10;

    private static final int VER_CUBE_GROW = 7;
    private static final int VER_TIME_CHANGE = 8;
    private static final int VER_LOOPING_DATA = 9;
    private static final int VER_INVERTED_ROT_XY = 10;

    public static AnimationInfo loadAnimation(InputStream stream) throws IOException {
        StudioInputStream buffer = new StudioInputStream(stream);
        RotationOrder current = RotationOrder.ZYX; //TODO: load from dca
        int version = buffer.readInt();
        AnimationInfo info = new AnimationInfo(version, current, readLoopingData(buffer, version));

        if(info.getVersion() < MINIMUM_VERSION) {
            throw new IOException("Animation Needs to be at least version: " + MINIMUM_VERSION + ". Got:" + info.getVersion());
        }
        if(info.getVersion() > MAXIMUM_VERSION) {
            throw new IOException("Animation is too advanced. Please update studio jar. Maximum supported:" + MAXIMUM_VERSION + ". Got:" + info.getVersion());
        }

        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            info.addKeyframe(readKeyframe(buffer, info.getVersion()));
        }

        readAnimationEvents(buffer, info.getAnimationEvents(), info.getVersion());

        info.generatedCachedData();

        return info;
    }

    private static KeyframeHeader.LoopingData readLoopingData(StudioInputStream buffer, int version) throws IOException {
        if(version >= VER_LOOPING_DATA && buffer.readBoolean()) {
            return new KeyframeHeader.LoopingData(
                buffer.readFloat(),
                buffer.readFloat(),
                buffer.readFloat()
            );
        }
        return null;
    }

    private static KeyframeInfo readKeyframe(StudioInputStream buffer, int version) throws IOException {
        KeyframeInfo keyframe = new KeyframeInfo(readTime(buffer, version), readTime(buffer, version), buffer.readInt());

        readKeyframeMap(buffer, keyframe.getRotationMap(), (float) (Math.PI/180));
        readKeyframeMap(buffer, keyframe.getPositionMap(), 1);
        if(version >= VER_CUBE_GROW) {
            readKeyframeMap(buffer, keyframe.getCubeGrowMap(), 1);
        }
        if(version < VER_INVERTED_ROT_XY) {
            for (float[] value : keyframe.getRotationMap().values()) {
                value[0] = -value[0];
                value[1] = -value[1];
            }
        }

        readProgressionPoints(buffer, keyframe.getProgressionPoints());

        //Sort the progression points by their x (time) element
        keyframe.getProgressionPoints().sort(Comparator.comparing(e -> e[0]));

        return keyframe;
    }

    private static float readTime(StudioInputStream buffer, int version) throws IOException {
        float time = buffer.readFloat();
        if(version < VER_TIME_CHANGE) {
            return time / 20F;
        }
        return time;
    }

    private static void readKeyframeMap(StudioInputStream buffer, Map<String, float[]> map, float modifier) throws IOException {
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            map.put(buffer.readString(), new float[] { buffer.readFloat()*modifier, buffer.readFloat()*modifier, buffer.readFloat()*modifier });
        }
    }

    private static void readProgressionPoints(StudioInputStream buffer, List<float[]> progressionPoints) throws IOException {
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            progressionPoints.add(new float[] {buffer.readFloat(), buffer.readFloat() } );
        }
    }

    private static void readAnimationEvents(StudioInputStream buffer, List<AnimationEventInfo> events, int version) throws IOException {
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            AnimationEventInfo info = new AnimationEventInfo(readTime(buffer, version));
            int dataSize = buffer.readInt();
            for (int d = 0; d < dataSize; d++) {
                info.getData().computeIfAbsent(buffer.readString(), s -> new ArrayList<>()).add(buffer.readString());
            }
            events.add(info);
        }
    }
}
