package com.sp.mixin;

import com.sp.render.PerspectiveRenderer;
import foundry.veil.api.client.render.VeilLevelPerspectiveRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VeilLevelPerspectiveRenderer.class)
public class VeilLevelPerspectiveRendererMixin {

    @Inject(method = "isRenderingPerspective", at = @At("RETURN"), cancellable = true, remap = false)
    private static void setReturn(CallbackInfoReturnable<Boolean> cir) {
        cir.cancel();
        cir.setReturnValue(PerspectiveRenderer.isRenderingPerspective());
    }

    @Inject(method = "getID", at = @At("RETURN"), cancellable = true, remap = false)
    private static void setReturn2(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(PerspectiveRenderer.getID());
    }

}
