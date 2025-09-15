package com.sp.destruction.server.custom;

import com.sp.DestroyingMinecraft;
import com.sp.destruction.server.ServerDestructionEvent;
import com.sp.entity.ModEntities;
import com.sp.entity.custom.MeteorEntity;
import com.sp.networking.ServerPacketManager;
import com.sp.util.keyframes.Keyframe;
import com.sp.util.keyframes.KeyframeAnimation;
import com.sp.world.spinningblockexplosion.custom.DirectionalSBE;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;

public class PlanetDestructionServer extends ServerDestructionEvent {
    private int meteorCooldown;
    private int trackingMeteorCooldown = 300;
    private final Random random = Random.create();
    private final Vec3d centerBlock = new Vec3d(-1503, 65, 1196);

    public PlanetDestructionServer() {
        super(2400);
    }

    @Override
    protected KeyframeAnimation initAnimations(World world) {
        return new KeyframeAnimation.KeyframeAnimationBuilder(
            this.duration,

            new Keyframe(0.0),

            new Keyframe(320.0/this.duration, (globalTime, localTime) -> {

                trackingMeteorCooldown--;
                if (meteorCooldown-- >= 0) return;

                double randX = (random.nextDouble()*2.0 - 1.0) * 100;
                double randZ = (random.nextDouble()*2.0 - 1.0) * 100;

                MeteorEntity meteor = ModEntities.METEOR_ENTITY.create(world);
                if (meteor != null) {
                    meteor.setPosition(centerBlock.add(randX + 120, 120, randZ));
                    world.spawnEntity(meteor);
                }

                if(trackingMeteorCooldown <= 0) {
                    List<PlayerEntity> playerList = (List<PlayerEntity>) world.getPlayers();
                    for (PlayerEntity player : playerList) {
                        if (!player.canTakeDamage()) continue;

                        MeteorEntity trackingMeteor = ModEntities.METEOR_ENTITY.create(world);
                        if (trackingMeteor != null) {
                            trackingMeteor.setPosition(player.getPos().add(120, 120, 0));
                            world.spawnEntity(trackingMeteor);
                        }
                    }
                    trackingMeteorCooldown = random.nextBetween(200, 300);
                }


                meteorCooldown = random.nextBetween(2, 5);
            }),

            new Keyframe(1800.0 / this.duration),

            new Keyframe(2350.0/this.duration, () -> {
                Vec3d averagePlayerPos = Vec3d.ZERO;
                List<PlayerEntity> players = (List<PlayerEntity>) world.getPlayers();
                int numOfTargetedPlayers = 0;

                for (PlayerEntity player : players) {
                    if (player.isCreative() || player.isSpectator()) continue;

                    averagePlayerPos = averagePlayerPos.add(player.getPos());
                    numOfTargetedPlayers++;
                }

                averagePlayerPos = averagePlayerPos.multiply(1.0f / numOfTargetedPlayers);

                DirectionalSBE directionalSBE = new DirectionalSBE(50, 50, -90, 0.5f, new Vec3d(averagePlayerPos.x, 67, averagePlayerPos.z));
                directionalSBE.beginExplosion((ServerWorld) world);
            })
        ).endAction(() -> {
            for (PlayerEntity player : world.getPlayers()) {
                ServerPacketManager.sendWaitingRoomPacket(player, true);
            }
        }).build();
    }
}
