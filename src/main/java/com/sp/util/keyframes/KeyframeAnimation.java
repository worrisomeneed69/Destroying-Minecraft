package com.sp.util.keyframes;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class KeyframeAnimation {
    private final List<Keyframe> keyframeList;
    private final Keyframe.KeyframeAction globalAction;
    private final Runnable endAction;
    private final int duration;
    public long startTime = -1L;
    private int progress;
    private boolean shouldSkipKeyframe;
    private Runnable keyframeSkippedCallback = () -> {};
    public int skippedTime;
    private final Int2IntArrayMap skippingCache = new Int2IntArrayMap();
    private int currentKeyframeIndex;

    private boolean endActionRan;

    private KeyframeAnimation(int duration, Keyframe.KeyframeAction globalAction, Runnable endAction, Keyframe... keyframes) {
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
    public void run(World world) {
        if (this.startTime == -1) {
            this.startTime = world.getTime();
        }

        this.progress = Math.toIntExact(world.getTime() - this.startTime) + this.skippedTime;
        if (this.shouldSkipKeyframe) {
            int nextKeyframeTime = (int) Math.floor(this.duration * this.getNextKeyframeTime());
            this.skippedTime += nextKeyframeTime - this.progress;
            this.skippingCache.put(Math.toIntExact(world.getTime()), this.skippedTime);
            this.shouldSkipKeyframe = false;
            this.keyframeSkippedCallback.run();
        }

        this.updateKeyframeAnimation((double) this.progress / this.duration);
    }

    /**
     * Call this method in an update loop to play the keyframe animation
     * @param time The time (0 -> 1) the animation should be played on
     */
    public void updateKeyframeAnimation(double time) {
        if (time >= 1.0) {
            if (!endActionRan) {
                endAction.run();
                endActionRan = true;
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


    /**
     * Should only be called for flashback compat
     */
    public void resetToCurrentTime(World world) {
        int worldTime = Math.toIntExact(world.getTime());

        //Early exit if the current time is before the animation starts
        if (world.getTime() < this.startTime) {
            this.resetAnimation();
            return;
        }

        //Set skipped time and new current progress
        this.skippedTime = 0;
        Map.Entry<Integer, Integer> maxEntry = null;
        for (Map.Entry<Integer, Integer> entry : this.skippingCache.int2IntEntrySet()) {
            if (entry.getKey() <= worldTime) {
                if (maxEntry == null || maxEntry.getKey() < entry.getKey()) {
                    maxEntry = entry;
                }
            }
        }

        if (maxEntry != null) {
            this.skippedTime = maxEntry.getValue();
        }

        this.progress = Math.toIntExact(world.getTime() - this.startTime) + this.skippedTime;

        //Set current keyframe index (list is sorted smallest keyframe time to largest)
        float time = (float) this.progress / this.duration;
        for (int i = 0; i < keyframeList.size(); i++) {
            Keyframe keyframe = keyframeList.get(i);
            if (i + 1 >= keyframeList.size()) {
                this.currentKeyframeIndex = i;
                break;
            }
            Keyframe nextKeyframe = keyframeList.get(i + 1);

            if (keyframe.getKeyframeTime() < time && nextKeyframe.getKeyframeTime() > time) {
                this.currentKeyframeIndex = i;
                break;
            }
        }

    }

    public void keyframeSkippedCallback(Runnable callback) {
        this.keyframeSkippedCallback = callback;
    }

    public double getNextKeyframeTime() {
        Keyframe nextKeyframe = this.getNextKeyframe();
        if (nextKeyframe == null) return 1.0;

        return nextKeyframe.getKeyframeTime();
    }

    public void skipKeyframe() {
        this.shouldSkipKeyframe = true;
    }

    public int getProgress() {
        return progress;
    }

    private Keyframe getCurrentKeyframe() {
        return keyframeList.get(currentKeyframeIndex);
    }

    private Keyframe getNextKeyframe() {
        return currentKeyframeIndex + 1 <= keyframeList.size() - 1 ? keyframeList.get(currentKeyframeIndex + 1) : null;
    }

    /**
     * If the timer doesn't reach 1.0, and you want to restart the animation, call this first
     */
    public void resetAnimation() {
        this.progress = 0;
        this.startTime = -1L;
        this.shouldSkipKeyframe = false;
        currentKeyframeIndex = 0;
        endActionRan = false;
        skippingCache.clear();
        this.skippedTime = 0;
        for (Keyframe keyframe : keyframeList) {
            keyframe.setInitialized(false);
        }
    }


    public static class KeyframeAnimationBuilder {
        private final int duration;
        private final Keyframe[] keyframeList;
        private Keyframe.KeyframeAction globalAction = (globalTime, localTime) -> {};
        private Runnable endAction = () -> {};

        public KeyframeAnimationBuilder(int duration, Keyframe... keyframes) {
            this.duration = duration;
            this.keyframeList = keyframes;
        }

        public KeyframeAnimationBuilder globalAction(Keyframe.KeyframeAction globalAction) {
            this.globalAction = globalAction;
            return this;
        }

        public KeyframeAnimationBuilder endAction(Runnable endAction) {
            this.endAction = endAction;
            return this;
        }

        public KeyframeAnimation build() {
            return new KeyframeAnimation(this.duration, this.globalAction, this.endAction, this.keyframeList);
        }

    }
}
