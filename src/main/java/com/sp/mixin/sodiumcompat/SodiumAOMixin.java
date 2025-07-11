package com.sp.mixin.sodiumcompat;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "net.minecraft.client.render.block.BlockModelRenderer$AmbientOcclusionCalculator")
public class SodiumAOMixin {

//    @Shadow @Final private float[] brightness;
//
//    @Inject(method = "apply", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/BlockRenderView;getBrightness(Lnet/minecraft/util/math/Direction;Z)F", shift = At.Shift.AFTER))
//    private void disableAO(BlockRenderView world, BlockState state, BlockPos pos, Direction direction, float[] box, BitSet flags, boolean shaded, CallbackInfo ci) {
//        if (!VeilRenderSystem.renderer().getLightRenderer().isAmbientOcclusionEnabled()) {
//            Arrays.fill(this.brightness, 1.0F);
//        }
//    }

}
