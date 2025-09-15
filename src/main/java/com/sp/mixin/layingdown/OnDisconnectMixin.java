package com.sp.mixin.layingdown;

import com.sp.mixininterfaces.LayingDownPlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class OnDisconnectMixin {

    @Inject(method = "onDisconnect", at = @At("TAIL"))
    private void getUpOnDisconnect(CallbackInfo ci) {
        if ((ServerPlayerEntity) (Object) this instanceof LayingDownPlayerEntity layingDownPlayerEntity && layingDownPlayerEntity.isLayingDown()) {
            layingDownPlayerEntity.getUp();
        }
    }

}
