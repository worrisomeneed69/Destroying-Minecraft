package com.sp.mixin;

import com.sp.entity.ModDamageSources;
import com.sp.mixininterfaces.PlayZoneEntity;
import com.sp.world.playzone.PlayZoneManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayZoneMixin extends Entity implements PlayZoneEntity {
    @Shadow public abstract boolean isInCreativeMode();

    @Shadow public abstract boolean isSpectator();

    @Shadow public abstract boolean damage(DamageSource source, float amount);

    @Unique private boolean insideAPlayZone = true;
    @Unique private long deathTime;

    public PlayZoneMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public long getDeathTime() {
        return this.deathTime;
    }

    @Override
    public boolean isInsidePlayZone() {
        return this.insideAPlayZone;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void setOutSidePlayZone(CallbackInfo ci) {
        boolean insidePlayZone;
        if (this.isInCreativeMode() || this.isSpectator()) {
            insidePlayZone = true;
            deathTime = 0L;
        } else {
            insidePlayZone = PlayZoneManager.isInsideAPlayZone(this.getPos());

            if (!insidePlayZone && deathTime == 0L) {
                deathTime = System.currentTimeMillis() + 7000L;
            } else if (insidePlayZone) {
                deathTime = 0L;
            }
        }

        if (!insidePlayZone && (deathTime - System.currentTimeMillis()) <= 0) {
            this.damage(ModDamageSources.of(this.getWorld(), ModDamageSources.PLAY_ZONE_DAMAGE_TYPE), Float.MAX_VALUE);
        }

        this.insideAPlayZone = insidePlayZone;
    }
}
