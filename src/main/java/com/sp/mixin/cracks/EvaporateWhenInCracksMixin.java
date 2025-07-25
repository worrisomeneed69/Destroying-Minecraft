package com.sp.mixin.cracks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.entity.PlayerComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class EvaporateWhenInCracksMixin {

    @WrapOperation(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;updatePostDeath()V"))
    private void disablePostDeath(LivingEntity instance, Operation<Void> original) {
        if (instance instanceof PlayerEntity player) {
            PlayerComponent component = InitializeComponents.PLAYERS.get(player);

            if (component.isInHole()) {
                return;
            }
        }

        original.call(instance);
    }

}
