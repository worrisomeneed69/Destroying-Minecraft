package com.sp.util.keyframes;

import java.util.Arrays;
import java.util.List;

public class KeyframeAnimation {
    private final List<Keyframe> keyframeList;
    private int currentKeyframeIndex = 0;

    public KeyframeAnimation(Keyframe... keyframes) {
        if (keyframes.length == 0) throw new RuntimeException("Cannot make a keyframe animation with zero keyframes");

        this.keyframeList = Arrays.stream(keyframes).sorted((o1, o2) -> {
            int comp = Float.compare(o1.getKeyframeTime(), o2.getKeyframeTime());
            if (comp == 0) throw new RuntimeException("Keyframes cannot have the same time value");

            return comp;
        }).toList();
    }

    /**
     * Call this method in an update loop to play the keyframe animation
     * @param time The time (0 -> 1) the animation should be played on
     */
    public void updateKeyframeAnimation(float time) {
        if (time >= 1.0) {
            this.resetAnimation();
            return;
        }

        Keyframe currentKeyframe = keyframeList.get(currentKeyframeIndex);
        Keyframe nextKeyframe = currentKeyframeIndex + 1 <= keyframeList.size() - 1 ? keyframeList.get(currentKeyframeIndex + 1) : null;

        if (nextKeyframe != null && nextKeyframe.getKeyframeTime() <= time ) {
            currentKeyframe = nextKeyframe;
            currentKeyframeIndex++;
            nextKeyframe = currentKeyframeIndex + 1 < keyframeList.size() - 1 ? keyframeList.get(currentKeyframeIndex + 1) : null;
        }

        if (!currentKeyframe.isInitialized()) {
            currentKeyframe.getInitAction().run();
            currentKeyframe.setInitialized(true);
        }

        float currentKeyframeTime = currentKeyframe.getKeyframeTime();
        float nextKeyframeTime = nextKeyframe != null ? nextKeyframe.getKeyframeTime() : 1.0f;

        currentKeyframe.getAction().run(time, (time - currentKeyframeTime) / (nextKeyframeTime - currentKeyframeTime));
    }

    /**
     * If the timer doesn't reach 1.0, and you want to restart the animation, call this first
     */
    public void resetAnimation() {
        currentKeyframeIndex = 0;
        for (Keyframe keyframe : keyframeList) {
            keyframe.setInitialized(false);
        }
    }
}
