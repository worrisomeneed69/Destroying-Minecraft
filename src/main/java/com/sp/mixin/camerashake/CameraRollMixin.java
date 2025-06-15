package com.sp.mixin.camerashake;

import com.llamalad7.mixinextras.sugar.Local;
import com.sp.render.camerashake.CameraShakeManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class CameraRollMixin {

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;tiltViewWhenHurt(Lnet/minecraft/client/util/math/MatrixStack;F)V"))
    public void renderWorld(RenderTickCounter tickCounter, CallbackInfo ci, @Local MatrixStack matrixStack){
        PlayerEntity player = this.client.player;

        if (player != null) {
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) CameraShakeManager.getTotalRoll()));
        }
    }

}
