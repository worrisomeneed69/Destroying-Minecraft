package com.sp.mixin.layingdown;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.sp.DestroyingMinecraftClient;
import com.sp.mixininterfaces.LayingDownPlayerEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Camera.class)
public class CameraMixin {

    @Shadow private Entity focusedEntity;

    @WrapOperation(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V"))
    private void setCameraPosition(Camera instance, double x, double y, double z, Operation<Void> original) {
        if (this.focusedEntity instanceof LayingDownPlayerEntity playerEntity && playerEntity.isLayingDown() && MinecraftClient.getInstance().options.getPerspective() == Perspective.FIRST_PERSON) {
            original.call(instance, x, y + 0.2, z);
        } else {
            original.call(instance, x, y, z);
        }
    }

    @WrapOperation(method = "setRotation", at = @At(value = "INVOKE", target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;"))
    private Quaternionf switchHorizontal(Quaternionf instance, float angleY, float angleX, float angleZ, Operation<Quaternionf> original, @Local(argsOnly = true, ordinal = 0) float yaw, @Local(argsOnly = true, ordinal = 1) float pitch) {
        if (this.focusedEntity instanceof LayingDownPlayerEntity layingDownPlayerEntity && layingDownPlayerEntity.isLayingDown()) {
            float localYRot = switch (layingDownPlayerEntity.getLayingDownDirection()) {
                case EAST -> 90;
                case SOUTH -> 0;
                case WEST -> -90;
                default -> 180;
            };

            return original.call(instance,angleY, angleX, angleZ).rotateLocalX((float) Math.toRadians(90)).rotateLocalY((float) Math.toRadians(localYRot));
        }

        return original.call(instance, angleY, angleX, angleZ);
    }

}
