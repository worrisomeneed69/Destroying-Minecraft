package com.sp.mixin;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientWorld.class)
public class NoDirectionalBrightnessMixin {

    @Inject(method = "getBrightness", at = @At("RETURN"), cancellable = true)
    private void setReturn(Direction direction, boolean shaded, CallbackInfoReturnable<Float> cir){
        cir.setReturnValue(1.0f);
    }

}
