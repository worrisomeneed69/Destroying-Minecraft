package com.sp.mixin.layingdown;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.sp.mixininterfaces.LayingDownPlayerEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientPlayerInteractionManager.class)
public class PreventBlockBreakingMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @WrapMethod(method = "updateBlockBreakingProgress")
    private boolean preventBlockBreaking(BlockPos pos, Direction direction, Operation<Boolean> original) {
        boolean bl = !(client.player instanceof LayingDownPlayerEntity layingDownPlayerEntity) || !layingDownPlayerEntity.isLayingDown();
        return bl && original.call(pos, direction);
    }

    @WrapMethod(method = "attackBlock")
    private boolean preventBlockBreaking2(BlockPos pos, Direction direction, Operation<Boolean> original) {
        boolean bl = !(client.player instanceof LayingDownPlayerEntity layingDownPlayerEntity) || !layingDownPlayerEntity.isLayingDown();
        return bl && original.call(pos, direction);
    }

}
