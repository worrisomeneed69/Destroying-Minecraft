package com.sp.mixin.layingdown;

import com.sp.mixininterfaces.LayingDownPlayerEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class RenderLyingDownPlayerMixin<T extends LivingEntity> {

    @Unique
    private T entity;

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    private void setEntity(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        entity = livingEntity;
    }

    @ModifyArg(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;setAngles(Lnet/minecraft/entity/Entity;FFFFF)V"), index = 4)
    private float modifyYawRotation(float value) {
        if (entity instanceof LayingDownPlayerEntity layingDownPlayerEntity) {
            if (layingDownPlayerEntity.isLayingDown()) {
                float delta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true);
                float yawRot = entity.getYaw(delta) - getYawCopy(layingDownPlayerEntity.getLayingDownDirection());
                return yawRot - 90;
            }
        }
        return value;
    }


    @Unique
    private static float getYawCopy(Direction direction) {
        return switch (direction) {
            case SOUTH -> 90.0F;
            case NORTH -> 270.0F;
            case WEST -> 180.0F;
            default -> 0.0F;
        };
    }
}
