package com.sp.mixin.collision;

import com.sp.entity.ModEntities;
import com.sp.entity.custom.BlockPhysicsEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Entity.class)
public abstract class PlatformCollisionMixin {

    /*
    @Inject(method = "collidesWith", at = @At("RETURN"), cancellable = true)
    public void collidesWith(Entity other, CallbackInfoReturnable<Boolean> cir) {
        if (other instanceof BlockPhysicsEntity blockPhysicsEntity) {
            cir.setReturnValue(blockPhysicsEntity.collides((Entity) (Object) this).collides());
        }
    }
     */

    @Inject(method = "adjustMovementForCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Lnet/minecraft/world/World;Ljava/util/List;)Lnet/minecraft/util/math/Vec3d;", at = @At("RETURN"), cancellable = true)
    private static void adjustMovementForCollisions(@Nullable Entity entity, Vec3d movement, Box entityBoundingBox, World world, List<VoxelShape> collisions, CallbackInfoReturnable<Vec3d> cir) {
        if (entity != null) {
            List<BlockPhysicsEntity> blockPhysicsEntities = world.getEntitiesByType(ModEntities.BLOCK_PHYSICS_ENTITY, entityBoundingBox.stretch(movement), (entity1) -> true);

            if (!blockPhysicsEntities.isEmpty()) {
                Vec3d adjustedMovement = movement;

                for (BlockPhysicsEntity blockPhysicsEntity : blockPhysicsEntities) {
                    if (blockPhysicsEntity != null) {

                        double yAxisCollision = blockPhysicsEntity.getYAxisCollision(entityBoundingBox.offset(adjustedMovement));

                        if (yAxisCollision < 0.5 && yAxisCollision > 1e-7) {
                            //blockPhysicsEntity.collidingEntities.add(entity);
                            adjustedMovement = adjustedMovement.add(0, yAxisCollision, 0);
                            adjustedMovement = adjustedMovement.add(blockPhysicsEntity.getVelocity());
                            continue;
                        }

                        Vec3d newAdjustment = blockPhysicsEntity.getBestCollisionOffset(entityBoundingBox, adjustedMovement);

                        if (Math.abs(movement.length() - adjustedMovement.length()) > 1e-7) {
                            //blockPhysicsEntity.collidingEntities.add(entity);
                            newAdjustment = adjustedMovement.add(blockPhysicsEntity.getVelocity());
                        }

                        adjustedMovement = newAdjustment;
                    }
                }

                if (adjustedMovement.length() > 1e-7 && Math.abs(movement.length() - adjustedMovement.length()) > 1e-7) {
                    if (adjustedMovement.y > movement.y) {
                        entity.setOnGround(true);
                    }

                    cir.setReturnValue(adjustedMovement);
                }
            }
        }
    }
}
