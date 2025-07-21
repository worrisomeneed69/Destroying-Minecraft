package com.sp.util.keyframes;


public class Keyframe {
    private boolean initialized;
    private final double keyframeTime;
    private final Runnable initAction;
    private final KeyframeAction action;

    /**
     * No actions taken. Meant to act as a delay
     * @param keyframeTime The point in time (0 -> 1) that the keyframe should be placed
     */
    public Keyframe(double keyframeTime) {
        this(keyframeTime, () -> {}, (globalTime, localTime) -> {});
    }

    /**
     * Only the update action runs
     * @param keyframeTime The point in time (0 -> 1) that the keyframe should be placed
     * @param action The action that should be called every time the keyframe is updated
     */
    public Keyframe(double keyframeTime, KeyframeAction action) {
        this(keyframeTime, () -> {}, action);
    }

    /**
     * Only the initialize action runs. One time only keyframe
     * @param keyframeTime The point in time (0 -> 1) that the keyframe should be placed
     * @param initAction The action that should be called once at the beginning of the keyframe
     */
    public Keyframe(double keyframeTime, Runnable initAction) {
        this(keyframeTime, initAction, (globalTime, localTime) -> {});
    }

    /**
     * Full keyframe with both an initializing action and update action
     * @param keyframeTime The point in time (0 -> 1) that the keyframe should be placed
     * @param initAction The action that should be called once at the beginning of the keyframe
     * @param action The action that should be called every time the keyframe is updated
     */
    public Keyframe(double keyframeTime, Runnable initAction, KeyframeAction action) {
        this.keyframeTime = keyframeTime;
        this.initAction = initAction;
        this.action = action;
    }

    protected KeyframeAction getAction() {
        return this.action;
    }

    protected Runnable getInitAction() {
        return this.initAction;
    }

    protected double getKeyframeTime() {
        return this.keyframeTime;
    }

    protected boolean isInitialized() {
        return this.initialized;
    }

    protected void setInitialized(boolean initialized) {
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
        void run(double globalTime, double localTime);
    }
}