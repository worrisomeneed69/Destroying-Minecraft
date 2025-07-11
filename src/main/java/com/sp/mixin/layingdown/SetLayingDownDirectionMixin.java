package com.sp.mixin.layingdown;

import com.sp.block.custom.ChairBlock;
import com.sp.mixininterfaces.LayingDownPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class SetLayingDownDirectionMixin extends Entity {


    public SetLayingDownDirectionMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "getSleepingDirection", at = @At("RETURN"), cancellable = true)
    private void setDirection(CallbackInfoReturnable<Direction> cir) {
        if ((LivingEntity) (Object) this instanceof LayingDownPlayerEntity layingDownPlayerEntity) {
            BlockPos layingDownBlockPos = layingDownPlayerEntity.getLayingDownPos().orElse(null);
            cir.setReturnValue(layingDownBlockPos != null ? ChairBlock.getDirection(this.getWorld(), layingDownBlockPos) : null);
        }
    }

}
