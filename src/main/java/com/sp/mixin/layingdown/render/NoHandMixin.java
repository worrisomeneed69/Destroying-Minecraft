package com.sp.mixin.layingdown.render;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.sp.mixininterfaces.LayingDownPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GameRenderer.class)
public class NoHandMixin {

    @WrapMethod(method = "renderHand")
    private void noHandWhenLayingDown(Camera camera, float tickDelta, Matrix4f matrix4f, Operation<Void> original) {
        Entity cameraEntity = camera.getFocusedEntity();
        if (cameraEntity instanceof LayingDownPlayerEntity layingDownPlayerEntity && layingDownPlayerEntity.isLayingDown()) {
            return;
        }

        original.call(camera, tickDelta, matrix4f);
    }


}
