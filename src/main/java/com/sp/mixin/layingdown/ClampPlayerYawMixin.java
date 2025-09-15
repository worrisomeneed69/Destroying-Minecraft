package com.sp.mixin.layingdown;

import com.sp.mixininterfaces.LayingDownPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class ClampPlayerYawMixin {

    @Shadow
    public abstract float getYaw();

    @Shadow
    public float prevYaw;

    @Shadow
    public abstract void setYaw(float yaw);

    @Shadow
    public abstract void setHeadYaw(float headYaw);

    @Shadow
    public abstract void setPitch(float pitch);

    @Shadow
    public abstract float getPitch();

    @Inject(method = "changeLookDirection", at = @At("TAIL"))
    private void clampLookDir(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
        if ((Entity) (Object) this instanceof LayingDownPlayerEntity layingDownPlayerEntity && layingDownPlayerEntity.isLayingDown()) {
            this.clampPassengerYaw();
        }
    }

    @Unique
    protected void clampPassengerYaw() {
        float f = MathHelper.wrapDegrees(this.getYaw() - 180.0f);
        float g = MathHelper.clamp(f, -90.0F, 90.0F);
        this.prevYaw += g - f;
        this.setYaw(this.getYaw() + g - f);
        this.setHeadYaw(this.getYaw());

        this.setPitch(MathHelper.clamp(this.getPitch(), -10.0F, 90.0F));
    }

}
