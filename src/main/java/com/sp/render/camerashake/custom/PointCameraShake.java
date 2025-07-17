package com.sp.render.camerashake.custom;

import com.sp.render.camerashake.AbstractCameraShakeInstance;
import foundry.veil.api.client.util.Easing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class PointCameraShake extends AbstractCameraShakeInstance {
    private final PlayerEntity player;
    private final Vec3d position;
    private final float strength;
    private final Easing easing;

    public PointCameraShake(BlockPos position, float strength, int duration, Easing easing) {
        this(position.toCenterPos(), strength, duration, easing);
    }

    public PointCameraShake(Vec3d position, float strength, int duration, Easing easing) {
        super(duration);
        this.position = position;
        this.strength = strength;
        this.easing = easing;
        this.player = MinecraftClient.getInstance().player;

    }

    @Override
    public void tick() {
        super.tick();

        if (this.player != null) {
            float distToCenter = 1.0f - ((float) Math.sqrt(this.player.getPos().squaredDistanceTo(this.position)) / (this.strength));
            float temp = this.strength * (1.0f - this.easing.ease((float) this.progress / this.duration));
            this.trauma = Math.max(temp * distToCenter, 0.0f);
        }
    }
}
