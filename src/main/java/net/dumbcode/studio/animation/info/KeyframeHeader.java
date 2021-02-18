package net.dumbcode.studio.animation.info;

public class KeyframeHeader {
    public static class LoopingData {
        private float start;
        private float end;
        private float duration;

        public LoopingData(float start, float end, float duration) {
            this.start = start;
            this.end = end;
            this.duration = duration;
        }

        public float getStart() {
            return start;
        }

        public void setStart(float start) {
            this.start = start;
        }

        public float getEnd() {
            return end;
        }

        public void setEnd(float end) {
            this.end = end;
        }

        public float getDuration() {
            return duration;
        }

        public void setDuration(float duration) {
            this.duration = duration;
        }
    }
}
