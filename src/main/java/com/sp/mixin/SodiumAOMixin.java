package com.sp.mixin;

import foundry.veil.api.client.render.VeilRenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.BitSet;

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
