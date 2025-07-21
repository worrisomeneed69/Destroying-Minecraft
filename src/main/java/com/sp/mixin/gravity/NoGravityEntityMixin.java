package com.sp.mixin.gravity;

import com.sp.DestroyingMinecraft;
import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.world.WorldDestructionEventsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class NoGravityEntityMixin {

    @Shadow public abstract World getWorld();

    @Redirect(method = "applyGravity", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d reduceGravity(Vec3d instance, double x, double y, double z) {
        WorldDestructionEventsComponent component = InitializeComponents.EVENTS.get(this.getWorld());
        Vec3d gravityDir = DestroyingMinecraft.getGravityDir();
        Vec3d velocity = new Vec3d(x, y, z).lerp(new Vec3d(x, y + gravityDir.y, gravityDir.z), component.getGravityLerp());
//        System.out.println(velocity);
        return instance.add(velocity);

    }

}
