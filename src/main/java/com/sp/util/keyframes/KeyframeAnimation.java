package com.sp.util.keyframes;

import java.util.Arrays;
import java.util.List;

public class KeyframeAnimation {
    private final List<Keyframe> keyframeList;
    private final int duration;
    public int progress;
    private boolean shouldSkipKeyframe;
    private boolean keyframeSkipped;
    private int currentKeyframeIndex = 0;
    private final Keyframe.KeyframeAction globalAction;
    private final Runnable endAction;
    private boolean endActionRun;

    public KeyframeAnimation(int duration, Keyframe... keyframes) {
        this(duration, (globalTime, localTime) -> {}, keyframes);
    }

    public KeyframeAnimation(int duration, Keyframe.KeyframeAction globalAction, Keyframe... keyframes) {
        this(duration, globalAction, () -> {}, keyframes);
    }
    public KeyframeAnimation(int duration, Runnable endAction, Keyframe... keyframes) {
        this(duration, (globalTime, localTime) -> {}, endAction, keyframes);
    }

    public KeyframeAnimation(int duration, Keyframe.KeyframeAction globalAction, Runnable endAction, Keyframe... keyframes) {
        if (keyframes.length == 0) throw new RuntimeException("Cannot make a keyframe animation with zero keyframes");

        this.keyframeList = Arrays.stream(keyframes).sorted((o1, o2) -> {
            int comp = Double.compare(o1.getKeyframeTime(), o2.getKeyframeTime());
            if (comp == 0) throw new RuntimeException("Keyframes cannot have the same time value");

            return comp;
        }).toList();

        this.duration = duration;
        this.globalAction = globalAction;
        this.endAction = endAction;
    }

    /**
     * Call this method every TICK to automatically update the animation.</br>
     * Do not call this method in the render loop
     */
    public void run() {
        this.progress++;
//        this.progress = 2250;
        if (keyframeSkipped) keyframeSkipped = false;
        if (this.shouldSkipKeyframe) {
            double nextKeyframeTime = this.getNextKeyframeTime();
            this.progress = (int) Math.floor(this.duration * nextKeyframeTime);
            this.shouldSkipKeyframe = false;
            this.keyframeSkipped = true;
        }

        this.updateKeyframeAnimation((double) this.progress / this.duration);
    }

    /**
     * Call this method in an update loop to play the keyframe animation
     * @param time The time (0 -> 1) the animation should be played on
     */
    public void updateKeyframeAnimation(double time) {
        if (time >= 1.0) {
            if (!endActionRun) {
                endAction.run();
                endActionRun = true;
            }
            return;
        }

        Keyframe currentKeyframe = this.getCurrentKeyframe();
        Keyframe nextKeyframe = this.getNextKeyframe();

        if (nextKeyframe != null && nextKeyframe.getKeyframeTime() <= time) {
            currentKeyframe = nextKeyframe;
            currentKeyframeIndex++;
            nextKeyframe = currentKeyframeIndex + 1 < keyframeList.size() - 1 ? keyframeList.get(currentKeyframeIndex + 1) : null;
        }

        if (!currentKeyframe.isInitialized()) {
            currentKeyframe.getInitAction().run();
            currentKeyframe.setInitialized(true);
        }

        double currentKeyframeTime = currentKeyframe.getKeyframeTime();
        double nextKeyframeTime = nextKeyframe != null ? nextKeyframe.getKeyframeTime() : 1.0;

        double localTime = (time - currentKeyframeTime) / (nextKeyframeTime - currentKeyframeTime);
        currentKeyframe.getAction().run(time, localTime);

        globalAction.run(time, time);
    }

    private Keyframe getCurrentKeyframe() {
        return keyframeList.get(currentKeyframeIndex);
    }

    private Keyframe getNextKeyframe() {
        return currentKeyframeIndex + 1 <= keyframeList.size() - 1 ? keyframeList.get(currentKeyframeIndex + 1) : null;
    }

    public double getNextKeyframeTime() {
        Keyframe nextKeyframe = this.getNextKeyframe();
        if (nextKeyframe == null) return 1.0;

        return nextKeyframe.getKeyframeTime();
    }

    public void skipKeyframe() {
        this.shouldSkipKeyframe = true;
    }

    public boolean wasKeyframeSkipped() {
        return keyframeSkipped;
    }

    public int getProgress() {
        return progress;
    }

    /**
     * If the timer doesn't reach 1.0, and you want to restart the animation, call this first
     */
    public void resetAnimation() {
        currentKeyframeIndex = 0;
        endActionRun = false;
        for (Keyframe keyframe : keyframeList) {
            keyframe.setInitialized(false);
        }
    }
}
