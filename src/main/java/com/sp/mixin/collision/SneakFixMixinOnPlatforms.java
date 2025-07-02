package com.sp.mixin.collision;

import com.sp.entity.ModEntities;
import com.sp.entity.custom.BlockPhysicsEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PlayerEntity.class)
public class SneakFixMixinOnPlatforms {

    @Inject(method = "isSpaceAroundPlayerEmpty", at = @At("HEAD"), cancellable = true)
    private void isSpaceAroundPlayerEmpty(double offsetX, double offsetZ, float f, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        Box box = player.getBoundingBox();

        Box newBox = new Box(box.minX + offsetX, box.minY - (double)f - 9.999999747378752E-6, box.minZ + offsetZ, box.maxX + offsetX, box.minY, box.maxZ + offsetZ);

        boolean doesNotCollide = player.getWorld().isSpaceEmpty(player, newBox);

        if (!doesNotCollide) {
            cir.setReturnValue(false);
        }

        List<BlockPhysicsEntity> blockPhysicsEntities = player.getWorld().getEntitiesByType(ModEntities.BLOCK_PHYSICS_ENTITY, newBox, (entity1) -> true);

        if (!blockPhysicsEntities.isEmpty()) {
            for (BlockPhysicsEntity blockPhysicsEntity : blockPhysicsEntities) {
                if (blockPhysicsEntity.collides(newBox)) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
