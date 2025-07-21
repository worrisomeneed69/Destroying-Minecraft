package com.sp.mixin.gravity;

import com.llamalad7.mixinextras.sugar.Local;
import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.world.WorldDestructionEventsComponent;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LimbAnimator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntityRenderer.class)
public class SlowLimbMovementsMixin {
    private static final String RENDER = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V";

    @Redirect(method = RENDER, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LimbAnimator;getPos(F)F"))
    private float slowerSpeed(LimbAnimator instance, float tickDelta, @Local(argsOnly = true) LivingEntity livingEntity) {
        if (!livingEntity.isOnGround() && !livingEntity.isInCreativeMode() && !livingEntity.isSpectator()) {
            WorldDestructionEventsComponent component = InitializeComponents.EVENTS.get(livingEntity.getWorld());
            return (float) MathHelper.lerp(component.getGravityLerp(), instance.getPos(tickDelta), 0.01);
        }
        return instance.getPos(tickDelta);
    }

}
