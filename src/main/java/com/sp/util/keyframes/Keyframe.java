package com.sp.util.keyframes;


public class Keyframe {
    private boolean initialized;
    private final float keyframeTime;
    private final Runnable initAction;
    private final KeyframeAction action;

    /**
     * No actions taken. Meant to act as a delay
     * @param keyframeTime The point in time (0 -> 1) that the keyframe should be placed
     */
    public Keyframe(float keyframeTime) {
        this(keyframeTime, () -> {}, (globalTime, localTime) -> {});
    }

    /**
     * Only the update action runs
     * @param keyframeTime The point in time (0 -> 1) that the keyframe should be placed
     * @param action The action that should be called every time the keyframe is updated
     */
    public Keyframe(float keyframeTime, KeyframeAction action) {
        this(keyframeTime, () -> {}, action);
    }

    /**
     * Only the initialize action runs. One time only keyframe
     * @param keyframeTime The point in time (0 -> 1) that the keyframe should be placed
     * @param initAction The action that should be called once at the beginning of the keyframe
     */
    public Keyframe(float keyframeTime, Runnable initAction) {
        this(keyframeTime, initAction, (globalTime, localTime) -> {});
    }

    /**
     * Full keyframe with both an initializing action and update action
     * @param keyframeTime The point in time (0 -> 1) that the keyframe should be placed
     * @param initAction The action that should be called once at the beginning of the keyframe
     * @param action The action that should be called every time the keyframe is updated
     */
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