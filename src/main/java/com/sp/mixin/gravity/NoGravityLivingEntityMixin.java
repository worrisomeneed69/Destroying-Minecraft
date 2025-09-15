package com.sp.mixin.gravity;

import com.sp.DestroyingMinecraft;
import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.world.WorldDestructionEventsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class NoGravityLivingEntityMixin extends Entity {

    @Shadow public abstract boolean isInCreativeMode();

    public NoGravityLivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @ModifyVariable(method = "computeFallDamage", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float reduceGravity(float value) {
        WorldDestructionEventsComponent component = InitializeComponents.EVENTS.get(this.getWorld());
        return (float) MathHelper.lerp(component.getGravityLerp() * 1.25, value, 0);
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(DDD)V", ordinal = 2))
    private void reduceGravity(LivingEntity instance, double x, double y, double z) {
        if (!this.isInCreativeMode() && !this.isSpectator()) {
            WorldDestructionEventsComponent component = InitializeComponents.EVENTS.get(this.getWorld());
            Vec3d gravityDir = WorldDestructionEventsComponent.gravityDir;

            Vec3d velocity = new Vec3d(x, y, z).lerp(new Vec3d(x, y + gravityDir.y, z + gravityDir.z), component.getGravityLerp());
            instance.setVelocity(velocity);
        } else {
            instance.setVelocity(x, y, z);
        }
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(DDD)V", ordinal = 3))
    private void reduceGravityFlutter(LivingEntity instance, double x, double y, double z) {
        if (!this.isInCreativeMode() && !this.isSpectator()) {
            WorldDestructionEventsComponent component = InitializeComponents.EVENTS.get(this.getWorld());
            Vec3d gravityDir = WorldDestructionEventsComponent.gravityDir;

            Vec3d velocity = new Vec3d(x, y, z).lerp(new Vec3d(x, y + gravityDir.y, z + gravityDir.z), component.getGravityLerp());
            instance.setVelocity(velocity);
        } else {
            instance.setVelocity(x, y, z);
        }
    }

}
