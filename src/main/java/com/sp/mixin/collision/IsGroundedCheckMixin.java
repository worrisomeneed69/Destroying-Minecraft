package com.sp.mixin.collision;

import com.sp.entity.ModEntities;
import com.sp.entity.custom.BlockPhysicsEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(Entity.class)
public abstract class IsGroundedCheckMixin {
    @Shadow public abstract World getWorld();

    @Shadow public abstract Box getBoundingBox();

    @Shadow public abstract void onLanding();

    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/BlockHitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void move(MovementType movementType, Vec3d movement, CallbackInfo ci, Vec3d vec3d, double d, BlockHitResult blockHitResult) {
        List<BlockPhysicsEntity> blockPhysicsEntities = this.getWorld().getEntitiesByType(ModEntities.BLOCK_PHYSICS_ENTITY, this.getBoundingBox().stretch(vec3d), (entity1) -> true);

        if (!blockPhysicsEntities.isEmpty()) {
            for (BlockPhysicsEntity blockPhysicsEntity : blockPhysicsEntities) {
                if (blockPhysicsEntity != null) {
                    if (blockPhysicsEntity.collides(this.getBoundingBox().stretch(vec3d))) {
                        this.onLanding();
                    }
                }
            }
        }
    }
}
