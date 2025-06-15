package com.sp.render.camerashake.custom;

import foundry.veil.api.client.util.Easing;

public class CameraShakeInstance {
    private float trauma;
    private final float strength;
    private final int duration;
    private int progress;
    private final Easing easing;


    public CameraShakeInstance(float strength, int duration, Easing easing) {
        this.strength = strength;
        this.duration = duration;
        this.easing = easing;
        this.progress = 0;
    }

    public void tick() {
        this.progress++;
        float temp = this.strength * (1.0f - this.easing.ease((float) this.progress / this.duration));
        this.trauma = Math.max(temp, 0.0f);
    }

    public float getTrauma() {
        return this.trauma;
    }

    public boolean isFinished() {
        return this.progress >= this.duration;
    }
}
