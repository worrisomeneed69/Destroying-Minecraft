package com.sp.mixin.quasar;

import foundry.veil.api.client.render.CullFrustum;
import foundry.veil.api.client.render.MatrixStack;
import foundry.veil.api.client.render.VeilLevelPerspectiveRenderer;
import foundry.veil.api.quasar.particle.ParticleSystemManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleSystemManager.class)
public class ParticleSystemManagerMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void noRenderOnShadowMap(MatrixStack matrixStack, VertexConsumerProvider bufferSource, Camera camera, CullFrustum frustum, float partialTicks, CallbackInfo ci) {
        if (VeilLevelPerspectiveRenderer.isRenderingPerspective()) {
            ci.cancel();
        }
    }

}
