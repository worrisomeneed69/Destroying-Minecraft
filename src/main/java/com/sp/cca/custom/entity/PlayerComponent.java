package com.sp.cca.custom.entity;

import com.sp.DestroyingMinecraft;
import com.sp.DestroyingMinecraftClient;
import com.sp.cca.InitializeComponents;
import com.sp.destruction.client.ClientDestructionEvent;
import com.sp.destruction.client.custom.LaserDestructionClient;
import com.sp.destruction.server.custom.LaserDestructionServer;
import com.sp.entity.ModDamageSources;
import com.sp.render.BlackScreenManager;
import com.sp.sounds.ModSounds;
import com.sp.util.Noise;
import com.sp.world.playzone.PlayZoneManager;
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
    private final PlayerEntity player;
    private boolean insideAPlayZone;
    private long deathTime;

    private boolean isInHole;
    private boolean spawnedEvaporateParticles;

    private boolean isInWaitingRoom;
    private boolean initWaitingRoom;

    public PlayerComponent(PlayerEntity player) {
        this.player = player;
        this.insideAPlayZone = true; //If this isn't set, it plays the countdown noise for a split second
    }


    public boolean isInsideAPlayZone() {
        return this.insideAPlayZone;
    }
    public long getDeathTime() {
        return this.deathTime;
    }

    public boolean isInHole() {
        return this.isInHole;
    }

    public boolean isInWaitingRoom() {
        return this.isInWaitingRoom;
    }
    public void setInWaitingRoom(boolean inWaitingRoom) {
        this.isInWaitingRoom = inWaitingRoom;
    }


    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        this.isInWaitingRoom = nbtCompound.getBoolean("isInWaitingRoom");
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        nbtCompound.putBoolean("isInWaitingRoom", this.isInWaitingRoom);
    }

    public void sync() {
        InitializeComponents.PLAYERS.sync(this.player);
    }

    @Override
    public void clientTick() {
        this.updateInAPlayZone();
        ClientDestructionEvent cracksDestructionEvent = DestroyingMinecraftClient.cracksPostShader.getDestructionEvent();
        if (cracksDestructionEvent.isActive()) {
            this.updateInHole(LaserDestructionClient.laserLength.getTimer(1.0f), LaserDestructionClient.cracksTime.getTimer(1.0f));
        }

        if (this.isInHole && !spawnedEvaporateParticles) {
            this.player.playSound(ModSounds.LAVA_DEATH, 1.0f, 1.0f);
            for (int i = 0; i < 100; i++) {
                double d = this.player.getRandom().nextGaussian() * 0.2;
                double f = this.player.getRandom().nextGaussian() * 0.2;
                this.player.getWorld().addParticle(
                        ParticleTypes.POOF,
                        this.player.getParticleX(1.0),
                        this.player.getRandomBodyY(),
                        this.player.getParticleZ(1.0),
                        d,
                        0,
                        f
                );
            }
            spawnedEvaporateParticles = true;
        } else if(!this.isInHole) {
            spawnedEvaporateParticles = false;
        }
    }

    @Override
    public void serverTick() {
        if (!initWaitingRoom) {
            if (!this.player.getDisplayName().getString().equals("SppacePotato")) {
                this.isInWaitingRoom = true;
                this.sync();
            }
            initWaitingRoom = true;
        }

//        this.isInWaitingRoom = false;
//        this.sync();

        this.updateInAPlayZone();
        if (DestroyingMinecraft.laserDestruction.isActive()) {
            this.updateInHole(LaserDestructionServer.laserLength, LaserDestructionServer.crackingTime);
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

    private void updateInHole(float laserLength, float cracksTime) {
        if (!player.isOnGround() || this.player.isInCreativeMode() || this.player.isSpectator()) {
            this.isInHole = false;
            return;
        }

        float time = 0.0f;
        if (laserLength >= 1.0) {
            time = (cracksTime*50) + 5.0f;
        }

        Vec3d playerPos = this.player.getPos();

        float holeSize = (float) (1.0 - Vector2d.distance(-1709, 1575, playerPos.x, playerPos.z)/time);

        float noise = Noise.getCrackNoise(new Vec3d(playerPos.x, 4, playerPos.z));
        this.isInHole = noise < holeSize;
    }
}
