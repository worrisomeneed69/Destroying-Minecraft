package com.sp.render.camerashake.custom;

import com.sp.render.camerashake.AbstractCameraShakeInstance;
import foundry.veil.api.client.util.Easing;

public class CameraShakeInstance extends AbstractCameraShakeInstance {
    private final float strength;
    private final Easing easing;


    public CameraShakeInstance(float strength, int duration, Easing easing) {
        super(duration);
        this.strength = strength;
        this.easing = easing;
    }

    public void tick() {
        super.tick();
        float temp = this.strength * (1.0f - this.easing.ease((float) this.progress / this.duration));
        this.trauma = Math.max(temp, 0.0f);
    }

}
