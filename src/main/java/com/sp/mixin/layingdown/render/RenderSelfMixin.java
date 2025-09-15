package com.sp.mixin.layingdown.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.sp.mixininterfaces.LayingDownPlayerEntity;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WorldRenderer.class)
public class RenderSelfMixin {

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isSleeping()Z"))
    private boolean isLayingDown(LivingEntity instance, Operation<Boolean> original) {
        boolean bl = original.call(instance);
        if (instance instanceof LayingDownPlayerEntity layingDownPlayerEntity) {
            return layingDownPlayerEntity.isLayingDown() || bl;
        }

        return bl;
    }

}
