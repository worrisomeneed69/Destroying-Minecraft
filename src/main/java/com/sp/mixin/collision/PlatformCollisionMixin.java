package com.sp.mixin.collision;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.sp.entity.ModEntities;
import com.sp.entity.custom.BlockPhysicsEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

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

    @WrapMethod(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;")
    private Vec3d adjustMovementForCollisions(Vec3d movement, Operation<Vec3d> original) {
        Entity entity = (Entity) (Object) this;
        World world = entity.getWorld();
        Box entityBoundingBox = entity.getBoundingBox();

        if (entity != null) {
            List<BlockPhysicsEntity> blockPhysicsEntities = world.getEntitiesByType(ModEntities.BLOCK_PHYSICS_ENTITY, entityBoundingBox.stretch(movement).expand(3), (entity1) -> true);

            if (!blockPhysicsEntities.isEmpty()) {
                Vec3d adjustedMovement = movement;

                 for (BlockPhysicsEntity blockPhysicsEntity : blockPhysicsEntities) {
                    if (blockPhysicsEntity != null) {

                        if (movement.y != 0) {
                            double yAxisCollision = blockPhysicsEntity.getYAxisCollision(entityBoundingBox.offset(adjustedMovement));
                            if (yAxisCollision < 0.1 && yAxisCollision > 1e-7) {
                                //blockPhysicsEntity.collidingEntities.add(entity);
                                adjustedMovement = adjustedMovement.add(0, yAxisCollision, 0);
                                adjustedMovement = adjustedMovement.add(blockPhysicsEntity.getVelocity());
                                continue;
                            }
                        }

                        Vec3d newAdjustment = blockPhysicsEntity.getBestCollisionOffset(entityBoundingBox, adjustedMovement);

                        if (Math.abs(movement.length() - adjustedMovement.length()) > 1e-7) {
                            //blockPhysicsEntity.collidingEntities.add(entity);
                            newAdjustment = adjustedMovement.add(blockPhysicsEntity.getVelocity());
                        }

                        adjustedMovement = newAdjustment;
                    }
                }

//                if (adjustedMovement.length() > 1e-7 && Math.abs(movement.length() - adjustedMovement.length()) > 1e-7) {  //Commenting out this literally fixed everything
                    if (adjustedMovement.y > movement.y) {
                        entity.setOnGround(true);
                    }

                    return original.call(adjustedMovement);
//                }
            }
        }
        return original.call(movement);
    }

    /*
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

     */
}
