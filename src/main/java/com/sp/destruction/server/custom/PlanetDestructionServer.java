package com.sp.destruction.server.custom;

import com.sp.destruction.server.ServerDestructionEvent;
import com.sp.entity.ModEntities;
import com.sp.entity.custom.MeteorEntity;
import com.sp.util.keyframes.Keyframe;
import com.sp.util.keyframes.KeyframeAnimation;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
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
        super(1800);
    }

    @Override
    protected KeyframeAnimation initAnimations(World world) {
        return new KeyframeAnimation(
                new Keyframe(0.0f),

                new Keyframe((float) 320/1800, (globalTime, localTime) -> {
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
                        System.out.println("SPAWNED TRACKING METEOR");
                        trackingMeteorCooldown = random.nextBetween(200, 300);
                    }


                    meteorCooldown = random.nextBetween(2, 5);
                })
        );
    }
}
