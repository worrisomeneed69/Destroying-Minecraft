package com.sp.cca.custom.entity;

import com.sp.DestroyingMinecraft;
import com.sp.DestroyingMinecraftClient;
import com.sp.entity.ModDamageSources;
import com.sp.util.Noise;
import com.sp.world.playzone.PlayZoneManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector2d;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

public class PlayerComponent implements AutoSyncedComponent, ClientTickingComponent, ServerTickingComponent {
    private PlayerEntity player;
    private boolean insideAPlayZone;
    private long deathTime;

    private boolean isInHole;
    private boolean spawnedEvaporateParticles;

    public PlayerComponent(PlayerEntity player) {
        this.player = player;
        this.insideAPlayZone = true; //If we don't do this it plays the countdown noise for a split second
    }


    public boolean isInsideAPlayZone() {
        return insideAPlayZone;
    }
    public long getDeathTime() {
        return deathTime;
    }

    public boolean isInHole() {
        return isInHole;
    }


    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {

    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {

    }

    @Override
    public void clientTick() {
        this.updateInAPlayZone();
        if (DestroyingMinecraftClient.cracksPostShader.getDestructionEvent().isActive()) {
            this.updateInHole();
        }

        if (this.isInHole && !spawnedEvaporateParticles) {
            for (int i = 0; i < 100; i++) {
                double d = this.player.getRandom().nextGaussian() * 0.2;
                double f = this.player.getRandom().nextGaussian() * 0.2;
                this.player.getWorld().addParticle(ParticleTypes.POOF, this.player.getParticleX(1.0), this.player.getRandomBodyY(), this.player.getParticleZ(1.0), d, 0, f);
            }
            spawnedEvaporateParticles = true;
        } else if(!this.isInHole) {
            spawnedEvaporateParticles = false;
        }
    }

    @Override
    public void serverTick() {
        this.updateInAPlayZone();
        if (DestroyingMinecraft.laserDestruction.isActive()) {
            this.updateInHole();
        }

        if (this.isInHole) {
            this.player.damage(ModDamageSources.of(this.player.getWorld(), ModDamageSources.CRACKS_DAMAGE_TYPE), Float.MAX_VALUE);
        }
    }



    private void updateInAPlayZone() {
        boolean insidePlayZone;
        if (this.player.isInCreativeMode() || this.player.isSpectator()) {
            insidePlayZone = true;
            this.deathTime = 0L;
        } else {
            insidePlayZone = PlayZoneManager.isInsideAPlayZone(this.player.getPos());

            if (!insidePlayZone && this.deathTime == 0L) {
                this.deathTime = System.currentTimeMillis() + 7000L;
            } else if (insidePlayZone) {
                this.deathTime = 0L;
            }
        }

        if (!insidePlayZone && (this.deathTime - System.currentTimeMillis()) <= 0) {
            this.player.damage(ModDamageSources.of(this.player.getWorld(), ModDamageSources.PLAY_ZONE_DAMAGE_TYPE), Float.MAX_VALUE);
        }

        this.insideAPlayZone = insidePlayZone;
    }

    private void updateInHole() {
        if (!player.isOnGround() || this.player.isInCreativeMode() || this.player.isSpectator()) {
            this.isInHole = false;
            return;
        }

        Vec3d playerPos = this.player.getPos();

        float holeSize = (float) (1.0 - Vector2d.distance(-1709, 1575, playerPos.x, playerPos.z)/20.0);

        float noise = Noise.getCrackNoise(new Vec3d(playerPos.x, 4, playerPos.z));
        this.isInHole = noise < holeSize;
    }
}
