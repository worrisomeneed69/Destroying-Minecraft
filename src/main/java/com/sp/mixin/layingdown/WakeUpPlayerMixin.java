package com.sp.mixin.layingdown;

import com.sp.mixininterfaces.LayingDownPlayerEntity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(ServerPlayerEntity.class)
public class WakeUpPlayerMixin {

    @Inject(method = "onDisconnect", at = @At("TAIL"))
    private void getUpOnDisconnect(CallbackInfo ci) {
        if ((ServerPlayerEntity) (Object) this instanceof LayingDownPlayerEntity layingDownPlayerEntity && layingDownPlayerEntity.isLayingDown()) {
            layingDownPlayerEntity.getUp();
        }
    }

    @Inject(method = "teleport(Lnet/minecraft/server/world/ServerWorld;DDDLjava/util/Set;FF)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;stopRiding()V", shift = At.Shift.AFTER))
    private void getUpOnTeleport(ServerWorld world, double destX, double destY, double destZ, Set<PositionFlag> flags, float yaw, float pitch, CallbackInfoReturnable<Boolean> cir) {
        if ((ServerPlayerEntity) (Object) this instanceof LayingDownPlayerEntity layingDownPlayerEntity && layingDownPlayerEntity.isLayingDown()) {
            layingDownPlayerEntity.getUp();
        }
    }

}
