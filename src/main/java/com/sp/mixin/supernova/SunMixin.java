package com.sp.mixin.supernova;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class SunMixin{
    @Unique MatrixStack sunViewMat;

    //@ModifyConstant(method = "renderSky", constant = @Constant(floatValue = 30.0F))
//    private float changeSunSize(float k) {
//        return SupernovaRenderer.getSupernovaTimer();
//    }


    @Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack$Entry;getPositionMatrix()Lorg/joml/Matrix4f;", ordinal = 2))
    private void getSunViewMatrix(Matrix4f matrix4f, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback, CallbackInfo ci, @Local MatrixStack matrixStack){
        sunViewMat = matrixStack;
    }
}
