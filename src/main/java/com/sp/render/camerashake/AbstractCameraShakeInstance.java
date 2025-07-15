package com.sp.render.camerashake;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class AbstractCameraShakeInstance {
    protected float trauma;
    protected int progress;
    protected final int duration;

    protected AbstractCameraShakeInstance(int duration) {
        this.duration = duration;
        this.progress = 0;
    }

    public void tick(){
        progress++;
    }

    public float getTrauma() {
        return this.trauma;
    }

    public boolean isFinished() {
        return this.progress >= this.duration;
    }
}
