package net.dumbcode.studio.animation.info;

public class KeyframeHeader {
    public static class LoopingData {
        private final float start;
        private final float end;
        private final float duration;

        public LoopingData(float start, float end, float duration) {
            this.start = start;
            this.end = end;
            this.duration = duration;
        }

        public float getStart() {
            return start;
        }

        public float getEnd() {
            return end;
        }

        public float getDuration() {
            return duration;
        }
    }
}
