package com.sp.cca.custom.entity;

import com.sp.DestroyingMinecraft;
import com.sp.cca.InitializeComponents;
import com.sp.cca.custom.world.WorldDestructionEventsComponent;
import com.sp.destruction.server.custom.LaserDestructionServer;
import com.sp.entity.ModDamageSources;
import com.sp.networking.ServerPacketManager;
import com.sp.sounds.ModSounds;
import com.sp.sounds.instances.FadingSoundInstance;
import com.sp.util.Noise;
import com.sp.world.playzone.PlayZoneManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.joml.Vector2d;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

public class PlayerComponent implements AutoSyncedComponent, ClientTickingComponent, ServerTickingComponent {
    private final PlayerEntity player;
    private boolean hasDied;
    private GameMode prevGameMode;
    private boolean insideAPlayZone;
    private long deathTime;

    private boolean isInHole;
    private boolean spawnedEvaporateParticles;

    private boolean isInWaitingRoom;
    private boolean initWaitingRoom;

    private boolean shouldGlitch;
    private int glitchTime;
    private FadingSoundInstance glitchSoundInstance;

    public PlayerComponent(PlayerEntity player) {
        this.player = player;
        this.insideAPlayZone = true; //If this isn't set, it plays the countdown noise for a split second
        this.isInWaitingRoom = false;
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

    public boolean hasDied() {
        return this.hasDied;
    }
    public void setHasDied(boolean hasDied) {
        this.hasDied = hasDied;
    }

    public int getGlitchTime() {
        return glitchTime;
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        this.isInWaitingRoom = nbtCompound.getBoolean("isInWaitingRoom");
        this.initWaitingRoom = nbtCompound.getBoolean("initWaitingRoom");
        this.isInHole = nbtCompound.getBoolean("isInHole");
        this.hasDied = nbtCompound.getBoolean("hasDied");
        this.shouldGlitch = nbtCompound.getBoolean("shouldGlitch");
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        nbtCompound.putBoolean("isInWaitingRoom", this.isInWaitingRoom);
        nbtCompound.putBoolean("initWaitingRoom", this.initWaitingRoom);
        nbtCompound.putBoolean("isInHole", this.isInHole);
        nbtCompound.putBoolean("hasDied", this.hasDied);
        nbtCompound.putBoolean("shouldGlitch", this.shouldGlitch);
    }

    public void sync() {
        InitializeComponents.PLAYERS.sync(this.player);
    }

    public void resetPlayer() {
        this.isInHole = false;
        this.hasDied = false;

        if (this.prevGameMode != null) {
            ((ServerPlayerEntity) this.player).changeGameMode(prevGameMode);
            prevGameMode = null;
        }
        this.sync();
    }

    @Override
    public void clientTick() {
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

        if (this.player == MinecraftClient.getInstance().player) {
            this.updateInAPlayZone();

            if (this.shouldGlitch) {
                if (glitchSoundInstance == null || glitchSoundInstance.isDone()) {
                    glitchSoundInstance = FadingSoundInstance.ambient(
                            ModSounds.GLITCH,
                            50,
                            true,
                            0,
                            1.0f,
                            1.0f
                    );
                    MinecraftClient.getInstance().getSoundManager().play(glitchSoundInstance);
                }
                this.glitchTime = Math.min(this.glitchTime + 1, 100);;
            } else {
                if (glitchSoundInstance != null) {
                    glitchSoundInstance.fadeOut();
//                    glitchSoundInstance = null;
                }
                this.glitchTime = Math.max(this.glitchTime - 1, 0);
            }
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

        if (this.hasDied && !this.player.isSpectator()) {
            if (prevGameMode == null) {
                prevGameMode = ((ServerPlayerEntity) this.player).interactionManager.getGameMode();
            }

            ((ServerPlayerEntity) this.player).changeGameMode(GameMode.SPECTATOR);
        }

        this.updateInAPlayZone();
        if (DestroyingMinecraft.laserDestruction.isActive()) {
            this.updateInHole(LaserDestructionServer.laserLength, LaserDestructionServer.crackingTime);
        }

        if (this.isInHole && !this.hasDied) {
            this.hasDied = true;
            this.sync();
            this.player.damage(ModDamageSources.of(this.player.getWorld(), ModDamageSources.CRACKS_DAMAGE_TYPE), Float.MAX_VALUE);
        }

        this.updateGlitchTimer();
    }

    private void updateGlitchTimer() {
        WorldDestructionEventsComponent component = InitializeComponents.EVENTS.get(this.player.getWorld());

        if (component.getCurrentDestructionEvent() == null ||
            !component.getCurrentDestructionEvent().equals(DestroyingMinecraft.blackHoleDestructionPart2) ||
            this.player.getPos().z > 46.0f
        ) {
            if (this.shouldGlitch) {
                this.shouldGlitch = false;
                this.sync();
            }
            glitchTime = 0;
        } else {
            if (!this.shouldGlitch) {
                this.shouldGlitch = true;
                this.sync();
            }
            glitchTime++;
            if (glitchTime == 100) {
                ServerPacketManager.sendWaitingRoomPacket(player, true);
            }

        }
    }


    private void updateInAPlayZone() {
        boolean insidePlayZone;
        if ((this.player.isInCreativeMode() || this.player.isSpectator()) && !this.hasDied && !this.isInWaitingRoom) {
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
//            this.player.kill();
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
