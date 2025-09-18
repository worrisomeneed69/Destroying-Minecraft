package com.sp.mixin.compat.sodium.clouds;

import net.caffeinemc.mods.sodium.client.render.immediate.CloudRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CloudRenderer.class, remap = false)
public class CloudRendererMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void disableSodiumClouds(Camera camera, ClientWorld level, Matrix4f projectionMatrix, MatrixStack poseStack, float ticks, float tickDelta, CallbackInfo ci) {
        ci.cancel();
    }

}