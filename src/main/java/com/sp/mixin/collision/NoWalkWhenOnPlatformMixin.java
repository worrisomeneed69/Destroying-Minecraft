package com.sp.mixin.collision;

import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BipedEntityModel.class)
public class NoWalkWhenOnPlatformMixin<T extends LivingEntity> {

    // FIXME: MAKE LEGS NOT MOVE WHEN ON PLATFORM
    /*
    @Unique
    private float limbSwing = 0.0f;

    @ModifyVariable(
            method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true
    )
    private float modifyLimbAngle(float limbAngle, T livingEntity, float originalLimbAngle,
                                  float limbDistance, float animationProgress, float headYaw, float headPitch) {
        return this.limbSwing;
    }


    @Inject(method = "animateModel(Lnet/minecraft/entity/LivingEntity;FFF)V", at = @At("HEAD"))
    public void animateModel(T livingEntity, float f, float g, float h, CallbackInfo ci) {
        List<BlockPhysicsEntity> blockPhysicsEntity = livingEntity.getWorld().getEntitiesByType(ModEntities.BLOCK_PHYSICS_ENTITY, livingEntity.getBoundingBox(), (entity) -> entity.collides(livingEntity.getBoundingBox().expand(0.1)));

        if (!blockPhysicsEntity.isEmpty()) {
            this.limbSwing = (float) (livingEntity.limbAnimator.getPos() - (livingEntity.limbAnimator.getSpeed() - blockPhysicsEntity.stream().map((physicsEntity) -> physicsEntity.getVelocity().length()).toList().getFirst() * 100) * (1.0F - h));
        }

        this.limbSwing = livingEntity.limbAnimator.getPos() - livingEntity.limbAnimator.getSpeed() * (1.0F - h);
    }

     */

    /*
    @Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At("HEAD"))
    public void setAngles(T livingEntity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {

        limbAngle = livingEntity.limbAnimator.getPos() - livingEntity.speed * (1.0F - animationProgress);
    }
    */
}
