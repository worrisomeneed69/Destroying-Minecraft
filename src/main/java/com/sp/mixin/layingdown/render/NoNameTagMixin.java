package com.sp.mixin.layingdown.render;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.sp.mixininterfaces.LayingDownPlayerEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderer.class)
public class NoNameTagMixin<T extends Entity> {

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IF)V"))
    private boolean noTags(EntityRenderer instance, T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float tickDelta) {
        if (entity instanceof LayingDownPlayerEntity layingDownPlayerEntity && layingDownPlayerEntity.isLayingDown()) {
            return MinecraftClient.getInstance().getCameraEntity() != entity;
        }

        return true;
    }

}
