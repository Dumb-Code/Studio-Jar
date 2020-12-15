package net.dumbcode.studio.animation.info;

import net.dumbcode.studio.model.RotationOrder;
import net.dumbcode.studio.util.ByteBuffer;
import net.dumbcode.studio.util.RotationReorder;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class AnimationLoader {
    public static int MINIMUM_VERSION = 6;

    public static AnimationInfo loadAnimation(InputStream stream) throws IOException {
        ByteBuffer buffer = new ByteBuffer(stream);
        RotationOrder current = RotationOrder.ZYX; //TODO: load from dca 
        AnimationInfo info = new AnimationInfo(buffer.readInt());
        if(info.getVersion() < MINIMUM_VERSION) {
            throw new IOException("Animation Needs to be at least version: " + MINIMUM_VERSION + ". Got:" + info.getVersion());
        }

        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            info.getKeyframes().add(readKeyframe(buffer, current));
        }

        readAnimationEvents(buffer, info.getAnimationEvents());

        return info;
    }

    private static KeyframeInfo readKeyframe(ByteBuffer buffer, RotationOrder current) throws IOException {
        KeyframeInfo keyframe = new KeyframeInfo(buffer.readFloat(), buffer.readFloat(), buffer.readInt());

        readKeyframeMap(buffer, keyframe.getRotationMap(current), (float) (Math.PI/180));
        readKeyframeMap(buffer, keyframe.getPositionMap(), 1);

        for (RotationOrder value : RotationOrder.values()) {
            if(value == current) {
                continue;
            }
            Map<String, float[]> map = keyframe.getRotationMap(value);
            keyframe.getRotationMap(current).forEach((name, values) ->
                map.put(name, RotationReorder.reorder(Arrays.copyOf(values, 3), current, value))
            );

        }

        readProgressionPoints(buffer, keyframe.getProgressionPoints());

        //Sort the progression points by their x (time) element
        keyframe.getProgressionPoints().sort(Comparator.comparing(e -> e[0]));

        return keyframe;
    }

    private static void readKeyframeMap(ByteBuffer buffer, Map<String, float[]> map, float modifier) throws IOException {
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            map.put(buffer.readString(), new float[] { buffer.readFloat()*modifier, buffer.readFloat()*modifier, buffer.readFloat()*modifier });
        }
    }

    private static void readProgressionPoints(ByteBuffer buffer, List<float[]> progressionPoints) throws IOException {
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            progressionPoints.add(new float[] {buffer.readFloat(), buffer.readFloat() } );
        }
    }

    private static void readAnimationEvents(ByteBuffer buffer, List<AnimationEventInfo> events) throws IOException {
        int size = buffer.readInt();
        for (int i = 0; i < size; i++) {
            AnimationEventInfo info = new AnimationEventInfo(buffer.readInt());
            int dataSize = buffer.readInt();
            for (int d = 0; d < dataSize; d++) {
                info.getData().computeIfAbsent(buffer.readString(), s -> new ArrayList<>()).add(buffer.readString());
            }
            events.add(info);
        }
    }
}
