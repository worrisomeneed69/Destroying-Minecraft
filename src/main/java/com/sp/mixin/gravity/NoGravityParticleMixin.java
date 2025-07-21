package com.sp.mixin.gravity;

import com.sp.DestroyingMinecraft;
import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.world.WorldDestructionEventsComponent;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Particle.class)
public class NoGravityParticleMixin {

    @Shadow protected float gravityStrength;

    @Shadow @Final protected ClientWorld world;

    @Redirect(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/Particle;gravityStrength:F", opcode = Opcodes.GETFIELD))
    private float reduceGravity(Particle instance) {
        WorldDestructionEventsComponent component = InitializeComponents.EVENTS.get(this.world);
        return (float) MathHelper.lerp(component.getGravityLerp(), this.gravityStrength, 0.00);
    }

}
