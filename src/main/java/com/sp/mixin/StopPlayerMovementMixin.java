package com.sp.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.entity.PlayerComponent;
import com.sp.render.BlackScreenManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class StopPlayerMovementMixin extends LivingEntity {

    protected StopPlayerMovementMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @WrapMethod(method = "travel")
    private void stopMoving(Vec3d movementInput, Operation<Void> original) {
        PlayerComponent component = InitializeComponents.PLAYERS.get((PlayerEntity) (Object) this);
        if (component.isInWaitingRoom()) {
            this.setVelocity(Vec3d.ZERO);
            return;
        } else if (this.getWorld().isClient && ((PlayerEntity) (Object) this).equals(MinecraftClient.getInstance().player)) {
            if (BlackScreenManager.isBlackScreen()) {
                this.setVelocity(Vec3d.ZERO);
                return;
            }
        }

        original.call(movementInput);
    }

}