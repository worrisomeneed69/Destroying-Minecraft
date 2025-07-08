package com.sp.util.keyframes;


public class Keyframe {
    private boolean initialized;
    private final float keyframeTime;
    private final Runnable initAction;
    private final KeyframeAction action;

    public Keyframe(float keyframeTime, Runnable initAction, KeyframeAction action) {
        this.keyframeTime = keyframeTime;
        this.initAction = initAction;
        this.action = action;
    }

    public KeyframeAction getAction() {
        return this.action;
    }

    public Runnable getInitAction() {
        return this.initAction;
    }

    public float getKeyframeTime() {
        return this.keyframeTime;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    @FunctionalInterface
    public interface KeyframeAction {
        /**
         * The action the keyframe should perform during the given time
         *
         * @param globalTime The time (from 0 -> 1) from the start of the animation to the end
         * @param localTime  The time (from 0 -> 1) from the start of the keyframe to the start of the next keyframe
         */
        void run(float globalTime, float localTime);
    }
}