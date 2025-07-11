package com.sp.mixin.layingdown;

import com.sp.mixininterfaces.LayingDownPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public abstract class SetSleepingPoseMixin {

    @Redirect(method = "updatePose", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSleeping()Z"))
    private boolean test(PlayerEntity instance) {
        boolean isSleeping = instance.isSleeping();
        if (instance instanceof LayingDownPlayerEntity layingDownPLayerEntity) {
            return isSleeping || layingDownPLayerEntity.isLayingDown();
        }

        return isSleeping;
    }

}
