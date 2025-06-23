package com.sp.mixin.collision;

import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BipedEntityModel.class)
public class NoWalkWhenOnPlatformMixin<T extends LivingEntity> {
/*
    @Unique
    private float limbSwing = 0.0f;

    @ModifyVariable(
            method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getVelocity()Lnet/minecraft/util/math/Vec3d;"
            )
    )
    private Vec3d modifyGetVelocityInSetAngles(LivingEntity entity) {
        List<BlockPhysicsEntity> blockPhysicsEntities = entity.getWorld().getEntitiesByType(ModEntities.BLOCK_PHYSICS_ENTITY, entity.getBoundingBox(), (entity1) -> true);

        if (!blockPhysicsEntities.isEmpty()) {
            for (BlockPhysicsEntity blockPhysicsEntity : blockPhysicsEntities) {
                if (blockPhysicsEntity != null) {
                    if (blockPhysicsEntity.collides(entity.getBoundingBox())) {
                        return entity.getVelocity().subtract(blockPhysicsEntity.getVelocity());
                    }
                }
            }
        }

        return entity.getVelocity();
    }

 */
}
