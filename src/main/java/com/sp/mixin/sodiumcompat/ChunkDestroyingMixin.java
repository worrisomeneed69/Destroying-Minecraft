package com.sp.mixin.sodiumcompat;

import com.sp.destruction.client.custom.supernova.SupernovaDestructionClient;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.caffeinemc.mods.sodium.client.render.chunk.occlusion.OcclusionCuller;
import net.caffeinemc.mods.sodium.client.render.viewport.Viewport;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = OcclusionCuller.class, remap = false)
public class ChunkDestroyingMixin {

    @Inject(method = "isSectionVisible", at = @At("RETURN"), cancellable = true)
    private static void test(RenderSection section, Viewport viewport, float maxDistance, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            int distToPlayer = section.getCenterX() - player.getBlockX();
            boolean bl = distToPlayer <= SupernovaDestructionClient.destructionDistance;
            cir.setReturnValue(cir.getReturnValue() && bl);
        }

    }

}
