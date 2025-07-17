package com.sp.render.camerashake.custom;

import com.sp.render.camerashake.AbstractCameraShakeInstance;
import foundry.veil.api.client.util.Easing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CameraShakeInstance extends AbstractCameraShakeInstance {
    private final float startStrength;
    private final float endStrength;
    private final Easing easing;

    public CameraShakeInstance(float startStrength, float endStrength, int duration, Easing easing) {
        super(duration);
        this.startStrength = startStrength;
        this.endStrength = endStrength;
        this.easing = easing;
    }

    public void tick() {
        super.tick();
        float time = this.easing.ease((float) this.progress / this.duration);
        float finalTrauma = MathHelper.lerp(time, this.startStrength, this.endStrength);
        this.trauma = Math.max(finalTrauma, 0.0f);
    }

}
