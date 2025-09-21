package com.sp.mixin.layingdown.render;

import com.sp.mixininterfaces.LayingDownPlayerEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class RenderLyingDownPlayerMixin<T extends LivingEntity, M extends EntityModel<T>> {
    @Shadow
    protected M model;
    @Unique private static final String RENDER = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V";
    @Unique private static final String SET_ANGLES = "Lnet/minecraft/client/render/entity/model/EntityModel;setAngles(Lnet/minecraft/entity/Entity;FFFFF)V";


    @Unique
    private T entity;

    @Inject(method = RENDER, at = @At("HEAD"))
    private void setEntity(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        entity = livingEntity;
    }

    @ModifyArg(method = RENDER, at = @At(value = "INVOKE", target = SET_ANGLES), index = 4)
    private float modifyYawRotation(float value) {
        if (entity instanceof LayingDownPlayerEntity layingDownPlayerEntity && layingDownPlayerEntity.isLayingDown()) {
            float delta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true);
            float yawRot = entity.getYaw(delta) - 90;
            return yawRot - 90;
        }

        return value;
    }

    @Inject(method = RENDER, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V"))
    private void makeBodyVisible(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        Entity cameraEntity = MinecraftClient.getInstance().cameraEntity;
        if (!(this.model instanceof PlayerEntityModel<?> playerEntityModel) ||
                !cameraEntity.equals(livingEntity) ||
                MinecraftClient.getInstance().options.getPerspective() != Perspective.FIRST_PERSON) return;

        if (entity instanceof LayingDownPlayerEntity layingDownPlayerEntity && layingDownPlayerEntity.isLayingDown()) {
            playerEntityModel.setVisible(true);
            playerEntityModel.head.visible = false;
            playerEntityModel.hat.visible = false;
        }
    }
}
