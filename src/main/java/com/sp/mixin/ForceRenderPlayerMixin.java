package com.sp.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class ForceRenderPlayerMixin extends Entity {

    public ForceRenderPlayerMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void forceRender(World world, BlockPos pos, float yaw, GameProfile gameProfile, CallbackInfo ci){
        this.ignoreCameraFrustum = true;
    }

}
