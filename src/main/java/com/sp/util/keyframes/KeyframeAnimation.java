package com.sp.util.keyframes;

import java.util.Arrays;
import java.util.List;

public class KeyframeAnimation {
    private final List<Keyframe> keyframeList;
    private int currentKeyframeindex = 0;

    public KeyframeAnimation(Keyframe... keyframes) {
        if (keyframes.length == 0) throw new RuntimeException("Cannot make a keyframe animation with zero keyframes");

        this.keyframeList = Arrays.stream(keyframes).sorted((o1, o2) -> {
            int comp = Float.compare(o1.getKeyframeTime(), o2.getKeyframeTime());
            if (comp == 0) throw new RuntimeException("Keyframes cannot have the same time value");

            return comp;
        }).toList();
    }

    public void updateKeyframeAnimation(float time) {
        if (time >= 1.0) {
            this.resetAnimation();
            return;
        }

        Keyframe currentKeyframe = keyframeList.get(currentKeyframeindex);
        Keyframe nextKeyframe = currentKeyframeindex + 1 <= keyframeList.size() - 1 ? keyframeList.get(currentKeyframeindex + 1) : null;

        if (nextKeyframe != null && nextKeyframe.getKeyframeTime() <= time ) {
            currentKeyframe = nextKeyframe;
            currentKeyframeindex++;
            nextKeyframe = currentKeyframeindex + 1 < keyframeList.size() - 1 ? keyframeList.get(currentKeyframeindex + 1) : null;
        }

        if (!currentKeyframe.isInitialized()) {
            currentKeyframe.getInitAction().run();
            currentKeyframe.setInitialized(true);
        }

        float currentKeyframeTime = currentKeyframe.getKeyframeTime();
        float nextKeyframeTime = nextKeyframe != null ? nextKeyframe.getKeyframeTime() : 1.0f;

        currentKeyframe.getAction().run(time, (time - currentKeyframeTime) / (nextKeyframeTime - currentKeyframeTime));
    }

    public void resetAnimation() {
        currentKeyframeindex = 0;
        for (Keyframe keyframe : keyframeList) {
            keyframe.setInitialized(false);
        }
    }
}
